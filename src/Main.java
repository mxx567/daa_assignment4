import algorithms.*;
void main() {
    try {
        Experiments.run();
        Tests.run();
    }
    catch (Exception e){
        System.out.println(e.getMessage());
    }
}