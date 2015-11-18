package com.example.androidchoi.jobdam.Manager;

import android.content.Context;
import android.widget.Toast;

import com.begentgroup.xmlparser.XMLParser;
import com.example.androidchoi.jobdam.Model.ArticleLab;
import com.example.androidchoi.jobdam.Model.MyCardLab;
import com.example.androidchoi.jobdam.Model.JobList;
import com.example.androidchoi.jobdam.Model.MyJobLab;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;


/**
 * Created by dongja94 on 2015-10-21.
 */
public class NetworkManager {
    private static NetworkManager instance;

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    AsyncHttpClient client;
    XMLParser parser;
    Gson gson;

    private NetworkManager(){
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client = new AsyncHttpClient();
            client.setSSLSocketFactory(socketFactory);
            client.setCookieStore(new PersistentCookieStore(MyApplication.getContext()));
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        parser = new XMLParser();
        gson = new Gson();
        client.setCookieStore(new PersistentCookieStore(MyApplication.getContext()));
    }
    public HttpClient getHttpClient() {
        return client.getHttpClient();
    }

    public interface OnResultListener<T> {
        public void onSuccess(T result);
        public void onFail(int code);
    }

    private static final String API_ADDRESS = "http://api.saramin.co.kr/job-search";
//    ?stock=kospi+kosdaq&sr=directhire&fields=posting-date+expiration-date+keyword-code+count&count=10

    private static final String STOCK = "kospi+kosdaq";
    private static final String SR = "directhire";
    private static final String FIELDS = "posting-date+expiration-date+keyword-code+count";
    private static final String COUNT = "10";

    // 사람인 api 불러오는 method
    public void getJobAPI(Context context, final OnResultListener<JobList> listener){
        final RequestParams params = new RequestParams();
        params.put("stock", STOCK);
        params.put("sr",SR);
        params.put("fields", FIELDS);
        params.put("count", COUNT);
        client.get(context, API_ADDRESS, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ByteArrayInputStream bais = new ByteArrayInputStream(responseBody);
                JobList jobList = parser.fromXml(bais, "jobs", JobList.class);
                listener.onSuccess(jobList);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                listener.onFail(statusCode);
            }
        });
    }

    private static final String SERVER = "http://52.69.235.46:3000";

    // 내가 담은 채용정보 불러오는 method
    private static final String SHOW_MY_JOB = SERVER + "/showmyscrap/%s";
    public void showMyJob(Context context, String userName, final OnResultListener<MyJobLab> listener){
        RequestParams params = new RequestParams();
        String url = String.format(SHOW_MY_JOB, userName);
        Header[] headers = new Header[1];
        headers[0] = new BasicHeader("Accept", "application/json");
        client.get(context, url, headers, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                // 싱글톤 MyJobList
               MyJobLab.get(MyApplication.getContext(), gson.fromJson(responseString.toString(), MyJobLab.class));
                Toast.makeText(MyApplication.getContext(),"Network Success", Toast.LENGTH_SHORT).show();
                listener.onSuccess(MyJobLab.get(MyApplication.getContext()));
            }
        });
    }
    // 채용정보 담기
    private static final String ADD_MY_JOB = SERVER + "/addscrap/%s";
    public void addMyJob(Context context, String userName, final String jsonString, final OnResultListener<String> listener) {
        RequestParams params = new RequestParams();
        String url = String.format(ADD_MY_JOB, userName);
        try {
            client.post(context, url, new StringEntity(jsonString), "application/json", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    listener.onFail(statusCode);
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    listener.onSuccess(responseString);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //메모 보기
    public static final String SHOW_MY_MEMO = SERVER + "/showmymemo/%s";
    public void showMyMemo(Context context, String userName, final OnResultListener<MyCardLab> listener){
        RequestParams params = new RequestParams();
        String url = String.format(SHOW_MY_MEMO, userName);
        Header[] headers = new Header[1];
        headers[0] = new BasicHeader("Accept", "application/json");
        client.get(context, url, headers, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                MyCardLab.get(MyApplication.getContext(), gson.fromJson(responseString.toString(), MyCardLab.class));
                listener.onSuccess(MyCardLab.get(MyApplication.getContext()));
            }
        });
    }
    // 메모 추가
    private static final String ADD_MEMO = SERVER + "/addmemo";
    public void addMemo(Context context, final String jsonString, final OnResultListener<String> listener){
//        Header[] headers = new Header[1];
//        headers[0] = new BasicHeader("Accept", "application/json");
        try {
            client.post(context, ADD_MEMO, new StringEntity(jsonString), "application/json", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable){

                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Toast.makeText(MyApplication.getContext(), "메모 추가!", Toast.LENGTH_SHORT).show();
                    listener.onSuccess(responseString);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    //메모 수정
    private static final String UPDATE_MEMO = SERVER + "/memo/update";
    public void updateMemo(Context context, final String jsonString, final OnResultListener<String> listener){
        try {
            client.post(context, UPDATE_MEMO, new StringEntity(jsonString), "application/json", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Toast.makeText(MyApplication.getContext(), "메모 수정!", Toast.LENGTH_SHORT).show();
                    listener.onSuccess(responseString);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //게시글 보기
    public static final String SHOW_ARTICLE = SERVER + "/boardlist/%s";
    public void showArticle(Context context, String userName, final OnResultListener<ArticleLab> listener){
        RequestParams params = new RequestParams();
        String url = String.format(SHOW_MY_MEMO, userName);
        Header[] headers = new Header[1];
        headers[0] = new BasicHeader("Accept", "application/json");
        client.get(context, SHOW_ARTICLE, headers, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                ArticleLab.get(MyApplication.getContext(), gson.fromJson(responseString.toString(), ArticleLab.class));
                listener.onSuccess(ArticleLab.get(MyApplication.getContext()));
            }
        });
    }

    // 게시글 추가
    private static final String ADD_ARTICLE = SERVER + "/addboard";
    public void addArticle(Context context, final String jsonString, final OnResultListener<String> listener){
//        Header[] headers = new Header[1];
//        headers[0] = new BasicHeader("Accept", "application/json");
        try {
            client.post(context, ADD_ARTICLE, new StringEntity(jsonString), "application/json", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable){

                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Toast.makeText(MyApplication.getContext(), "게시글 추가!", Toast.LENGTH_SHORT).show();
                    listener.onSuccess(responseString);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    //게시글 수정
    private static final String UPDATE_ARTICLE = SERVER + "/board/update";
    public void updateArticle(Context context, final String jsonString, final OnResultListener<String> listener){
        try {
            client.post(context, UPDATE_ARTICLE, new StringEntity(jsonString), "application/json", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Toast.makeText(MyApplication.getContext(), "게시글 수정!", Toast.LENGTH_SHORT).show();
                    listener.onSuccess(responseString);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void cancelAll(Context context) {
        client.cancelRequests(context, true);
    }

//    ThreadPoolExecutor mExecutor;
//    public static final int CORE_POOL_SIZE = 5;
//    public static final int MAXIMUN_POOL_SIZE = 64;
//    public static final int KEEP_ALIVE_TIME = 5000;
//    LinkedBlockingQueue<Runnable> mRequestQueue = new LinkedBlockingQueue<Runnable>();
//    Map<Context, List<NetworkRequest>> mRequestMap = new HashMap<Context, List<NetworkRequest>>();
//
//    private NetworkManager() {
//        mExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUN_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, mRequestQueue);
//    }
//
//    public interface OnResultListener<T> {
//        public void onSuccess(NetworkRequest<T> request, T result);
//        public void onFail(NetworkRequest<T> request, int code);
//    }
//
//    public static final int MESSAGE_SEND_SUCCESS = 1;
//    public static final int MESSAGE_SEND_FAIL = 2;
//
//    Handler mHandler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            NetworkRequest request = (NetworkRequest) msg.obj;
//            switch (msg.what) {
//                case MESSAGE_SEND_SUCCESS:
//                    request.sendSuccess();
//                    break;
//                case MESSAGE_SEND_FAIL:
//                    request.sendFail();
//                    break;
//            }
//            removeMap(request);
//        }
//    };
//
//    public void sendSuccess(NetworkRequest request) {
//        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_SEND_SUCCESS, request));
//    }
//
//    public void sendFail(NetworkRequest request) {
//        mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_SEND_FAIL, request));
//    }
//
//    public <T> void getNetworkData(Context context, NetworkRequest<T> request, OnResultListener<T> listener) {
//        request.setOnResultListener(listener);
//        getNetworkData(context, request);
//    }
//
//    public void cancelAll(Context context) {
//        List<NetworkRequest> list = mRequestMap.get(context);
//        if (list != null) {
//            List<NetworkRequest> removelist = new ArrayList<NetworkRequest>(list);
//            for (NetworkRequest req : removelist) {
//                req.cancel();
//            }
//        }
//    }
//
//    private void addMap(NetworkRequest request) {
//        Context context = request.getContext();
//        List<NetworkRequest> list = mRequestMap.get(context);
//        if (list == null) {
//            list = new ArrayList<NetworkRequest>();
//            mRequestMap.put(context, list);
//        }
//        list.add(request);
//    }
//
//    private void removeMap(NetworkRequest request) {
//        Context context = request.getContext();
//        List<NetworkRequest> list = mRequestMap.get(context);
//        if (list != null) {
//            list.remove(request);
//            if (list.size() == 0) {
//                mRequestMap.remove(context);
//            }
//        }
//    }
//
//    public <T> void getNetworkData(Context context, NetworkRequest<T> request) {
//        request.setNetworkManager(this);
//        request.setContext(context);
//        addMap(request);
//        mExecutor.execute(request);
//    }
//
//    public void processCancel(NetworkRequest request) {
//        mRequestQueue.remove(request);
//        removeMap(request);
//    }
//
//
//
//
//
    public void login(String userid, String password) {
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                OnResultListener<String> listener = null;
//                listener.onSuccess(null, "ok");
//            }
//        }, 1000);
    }

    public void signup(String userid, String password) {
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                OnResultListener<String> listener = null;
//                listener.onSuccess(null, "ok");
//            }
//        }, 1000);
    }
}
