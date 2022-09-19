class App{
    public static void main(String[] args) {
        Converter SimpleTest = new Converter();
        SimpleTest.readFile("Test.txt");
        SimpleTest.convert();
        SimpleTest.createFile("Out");
        SimpleTest.writeToFile();
        
    }
}