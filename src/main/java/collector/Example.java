package collector;


public class Example {
    public static void main(String[] args) throws Exception {
        // extends AbstractCollector to use the project
        AbstractCollector crawler = new Crawler();
        crawler.run();
    }
}