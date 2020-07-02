import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class data {
    public static void getDatasync(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
                    Request request = new Request.Builder()
                            .url("http://120.27.243.108:1240/weiboService/getUserWeibo?userid=18519860450&pageNum=0&pageSize=100")//请求接口。如果需要传参拼接到接口后面。
                            .build();//创建Request 对象
                    Response response = null;
                    response = client.newCall(request).execute();//得到Response 对象
                    if (response.isSuccessful()) {
                        System.out.println(response.body().string());
                    }
                    else{
                        System.out.println("error");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
