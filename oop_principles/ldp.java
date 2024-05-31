import java.util.List;

/**
 * 迪米特法则：Law of Demete
 * 一个类应对其他类有最少的了解，只和直接的朋友交流，甚至可以增加一个第三者，来介入两个类之间。
 */
public class ldp {
}

//  只和最直接的朋友交流或者增加一个第三者
// 举例：老师让班长清点学生花名册。三个对象：Teacher，Student， Monitor。 老师和班长是直接的朋友，班长和学生是直接的朋友。 老师和学生之间有班长来介入。

class Teacher{
    // 面向过程
    // 这里Teacher类了解了Student类
    int getStuCnt(Monitor m, List<Student> stus){
        // return m.Count(stus)
    }

    // 通过班长了解人数
    // 该方法不关心Student类
    int getStuCnt(Monitor m){

        // return m.CountStus()
    }
}
class Monitor{
}
class Student{}





