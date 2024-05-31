/**
 * 单一指责 single responsibility principle
 * <p>
 * 一个类或接口或方法应该只能有一个让它变化的原因，只负责一个职责，各个指责的代码改动不会影响其他程序
 * 如果多个职责耦合在一起，当某个职责代码变动时，可能会影响其他职责的代码变化
 * <p>
 * 解决：解耦
 */


// 举例: 用户可以执行登录操作，修改密码操作

// 错误示范
class srp_function_bad {
    // 用户操作
    void UserOperate(String operateType, String args) {
        if (operateType.equals("登录")) {
            // 执行登录操作
        }

        if (operateType.equals("修改密码")) {
            // 改密码
        }
    }
    // 这里将登录和修改密码两件事放在一个方法里面，仅依靠客户端传来的参数进行区分，如果参数传错会导致程序错误，
    // 而且 两个方法耦合在一起，未来修改和扩展不方便
}

class srp_function{
    void UserOperateLogin(){}

    void UserOperateResetPwd(){}
    // 将方法分职责分开，单一职责
}

// 接口 错误示范
interface srp_interface_bad{
    void login();
    void resetPwd();
}

class srp_interface_cls_login_bad implements srp_interface_bad{

    @Override
    public void login()
    {
        // 实现
    }

    @Override
    public void resetPwd() {
        // 因为该类是login类，所以该方法为空
    }

    // 因为srp_interface_bad将login，resetPwd方法放一起，导致在写login类时，需要实现resetPwd方法
}


// 举例:用户在网站可以登录，注册，修改密码操作

// 错误示范
class srp_class_bad{
    void login(){};
    void register(){};
    void resetPwd(){};
}

// 正确示范 面向接口编程
interface UserOperate{
    void operate();
}

// 登录类
class Login implements UserOperate{

    @Override
    public void operate() {

    }
}

// 注册类
class Register implements UserOperate{

    @Override
    public void operate() {

    }
}

// 对类而言，类的职责有大有小，根据业务逻辑确定即可。
// 这里并不是说srp_class_bad写法就是错的，当业务逻辑很简单时，放在srp_class_bad里面反而很方便。如果业务逻辑复杂，再考虑将类职责细分成UserOperate