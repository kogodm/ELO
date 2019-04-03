
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        EloRatingSystemDemo e=new EloRatingSystemDemo();

        e.show();

//        User u=e.gamer.get(new User().level).get(0);
//
//        for(int i=0;i<999;i++){
//            e.play(u);
//        }
//
//        e.show();
//
//        System.out.println(u.rating);

        //大部分情况下总能选出一个或少数几个等级比较高的（或比较低的...） 是这个算法比较神奇还是我的程序有问题呢...
        for(int i=0;i<99999;i++){
            User u=e.randGetUser();
            e.play(u);
        }

        e.show();

    }

}

class EloRatingSystemDemo {

    public List<List<User>> gamer=new ArrayList<>();
    //k值越大，升级就越快，绝大部分都处于越高位置
    public double k=100;

    public EloRatingSystemDemo() {
        //十个级别
        for(int i=0;i<10;i++){
            gamer.add(new ArrayList<User>());
        }

        //玩家
        for(int i=0;i<1000;i++){
            User u=new User();
            gamer.get(u.level).add(u);
        }
    }

    //为传入的玩家找到一个对手并开玩一局
    public void play(User user){
        User river=user;
        while(river==user){
            river=findRival(user);
        }
        fightAndRating(user,river);
    }

    //战斗并且评分(Elo Rating System)
    public void fightAndRating(User u1,User u2){

        //暂时移除
        gamer.get(u1.level).remove(u1);
        gamer.get(u2.level).remove(u2);

        //期望得分
        double ea=1.0/(1+Math.pow(10,(u1.rating-u2.rating)/400.0));
        double eb=1.0/(1+Math.pow(10,(u2.rating-u1.rating)/400.0));

        //发生战斗...结果未知
        int t=new Random().nextInt(2);

        double t2=0;
        if(t==0){
            //A赢
            t2=u1.rating+k*(1-ea);
            u1.rating=t2>0?t2:0;
            t2=u2.rating+k*(0-eb);
            u2.rating=t2>0?t2:0;
        }else if(t==1){
            //B赢
            t2=u1.rating+k*(0-ea);
            u1.rating=t2>0?t2:0;
            t2=u2.rating+k*(1-eb);
            u2.rating=t2>0?t2:0;
        }else if(t==2){
            //战平
            t2=u1.rating+k*(0-ea);;
            u1.rating=t2>0?t2:0;
            t2=u2.rating+k*(0-eb);
            u2.rating=t2>0?t2:0;
        }

//        System.out.println(u1.rating + "--" + u2.rating);
        //放入
        int level=(int) (u1.rating/500);
        level=level<10?level:9;
        u1.level=level;
        gamer.get(u1.level).add(u1);

        level=(int) (u2.rating/500);
        level=level<10?level:9;
        u2.level=level;
        gamer.get(u2.level).add(u2);

    }

    //找到一个级别相当(左右偏移，实力最接近)的对手
    public User findRival(User user){
        //如果当前级别只有自己一个人的话就偏移，否则的话说明可以找到同级别的玩家
        int shift=gamer.get(user.level).size()==1?1:0;
        while(true){
            boolean exit=true;
            //优先匹配弱一些的对手
            if(user.level-shift>=0){
                User u=findRival0(user.level-shift);
                if(u!=null) return u;
                exit=false;
            }
            if(user.level+shift<10){
                User u=findRival0(user.level+shift);
                if(u!=null) return u;
                exit=false;
            }

            if(exit) return null;

            shift++;
        }
    }

    //500分为一个级别，找在某个级别的对手
    private User findRival0(int level){
        //检测这个级别是否有人
        List<User> list=gamer.get(level);
        if(list.isEmpty()) return null;
        //随机选取一个对手
        Random r = new Random();
        r.setSeed(System.currentTimeMillis());
        return list.get(r.nextInt(list.size()));
    }

    //随机获得一个用户
    public User randGetUser(){
        while(true){
            Random r = new Random();
            r.setSeed(System.currentTimeMillis());
            List<User> list=gamer.get(r.nextInt(gamer.size()));
            if(!list.isEmpty()) return list.get(new Random().nextInt(list.size()));
        }
    }

    //打印所有玩家的信息：
    public void show(){
        for(int i=0;i<gamer.size();i++){
            List<User> list=gamer.get(i);
            System.out.printf("Level %2d: ,%4d :",i+1,list.size());
            for(int j=0;j<list.size();j++){
                System.out.printf("%.2f ",list.get(j).rating);
            }
            System.out.println();
        }
        System.out.println();
    }

}

//代表一个玩家，初始分数为1500
class User {
    double rating=1500;
    int level=(int) (rating/500);
}