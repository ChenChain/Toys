/**
 * 依赖倒置：Dependence Inversion Principle,
     高层模块不应该依赖低层模块，两者都应该依赖其抽象
     要针对接口编程，不要针对实现编程
 */
public class dip {
}

// 举例： 用户开小轿车
class Car{
    void run(){
        // car run
    }
}

class User1{
    void Drive(Car c) {
        c.run();
    }
}

// 结果用户需要开卡车，那么User类演变成
class Truck{
    void run(){
        // truck run
    }
}
class User2{
    void Drive(Car c) {
        c.run();
    }
    void Drive(Truck t){
        t.run();
    }

    // 后续每增加一个车型，都需要在用户类中添加对应当代码
}

// 上述是User依赖车型，面向实现编程。现在把它们抽象出来，面向接口编程
interface ICar {
    void run();
}
interface IUser {
    void drive(ICar c);
}

class Car1 implements ICar{

    @Override
    public void run() {

    }
}

class Car2 implements ICar{

    @Override
    public void run() {

    }
}

class User3 implements IUser{

    @Override
    public void drive(ICar c) {

    }
}

class DipTest{
    void test(){
        IUser u = new User3();
        ICar c1 = new Car1();
        ICar c2 = new Car2();
        u.drive(c1); // 依赖抽象
        u.drive(c2);
    }
}