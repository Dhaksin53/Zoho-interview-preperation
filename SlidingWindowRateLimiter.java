// In this we are using the sliding window approach

import java.util.*;
public class SlidingWindowRateLimiter{
    private final int maxRequests;
    private final long timeWindowMillis;
    private final Map<String,Deque<Long>> userRequestLog;
    public SlidingWindowRateLimiter(int maxRequests, long timeWindowSeconds) {
        this.maxRequests = maxRequests;
        this.timeWindowMillis = timeWindowSeconds*1000L;
        this.userRequestLog= new HashMap<>();
    }

    public synchronized boolean allowRequest(String userId){
        Long currentTime=System.currentTimeMillis();
        userRequestLog.putIfAbsent(userId,new LinkedList<>());

        Deque<Long> timestamps = userRequestLog.get(userId);
        while(!timestamps.isEmpty() && currentTime-timestamps.peekFirst()>timeWindowMillis){
            timestamps.pollFirst();
        }
        if(timestamps.size()<maxRequests){
            timestamps.offerLast(currentTime);
            return true;
        }else{
            return false;
        }
    }

    public class Main{
        public static void main(String[] args) throws InterruptedException{
            SlidingWindowRateLimiter limiter=new SlidingWindowRateLimiter(10, 600);
            String user="user123";
            for(int i=0;i<12;i++){
                if(limiter.allowRequest(user)){
                    System.out.println("Request allowed");
                }else{
                    System.out.println("request denied");
                }
            }
            Thread.sleep(500);

        }
    }
}
