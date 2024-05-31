/**
 * 里氏替换：Liskov Substitution principle
 * 里氏替换针对继承而言，程序中出现父类的地方，都可以替换成子类，并且不会影响逻辑：
     子类可以实现父类的抽象方法，但不能覆盖父类的非抽象方法
     子类中可以增加自己特有的方法
     当子类的方法重载父类的方法时，方法的前置条件（即方法的输入参数）要比父类的方法更宽松
     当子类的方法实现父类的方法时（重写/重载或实现抽象方法），方法的后置条件（即方法的的输出/返回值）要比父类的方法更严格或相等
    尽量不要从可实例化的父类中继承，而是从接口或者抽象类继承（这样程序中就不存在实例化的父类了）
 */


// 举例 lsp实现两数相减
public class lsp {
    int reduce(int a, int b) throws Exception{
        return a-b;
    }
}


// lsp_bad实现两数相减，两数相加功能
class lsp_bad extends lsp{
    int add(int a, int b){
        return a + b;
    }

    @Override
    int reduce(int a, int b) throws Exception {
        if (a > 10) {
            throw new Exception("a>10");
        }
        return super.reduce(a, b);
    }
}

// lsp_bad 因为重写父类方法时， 增加了一个限制条件，会导致原程序中，将lsp类替换成lsp_bad后，当a>10时程序异常