import java.io.Reader;
import java.lang.module.ModuleDescriptor.Opens;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.io.File;
import java.io.FileNotFoundException; 
import java.util.Scanner;
import java.io.IOException;

import javax.xml.crypto.Data;
import java.io.FileWriter; 

public class Converter {
private String data="";
private String fileName="";
public void convert(){
    CharacterIterator iter = new StringCharacterIterator(this.data);
        String text = new String("");
        for(char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
            if (Character.isUpperCase(c)) {
                text = text + Character.toLowerCase(c);
            } else if(Character.isLowerCase(c)) {
                text = text + Character.toUpperCase(c);                
            } else{
                text = text + " ";
            }

    }
    this.data = text;
}

public void createFile(String name) {
    try {
      File myObj = new File(name + ".txt");
      if (myObj.createNewFile()) {
        System.out.println("File created: " + myObj.getName());
      } else {
        System.out.println("File already exists.");
      }
      this.fileName = myObj.getName();
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

public void writeToFile (){
    try {
        FileWriter fileWriter = new FileWriter(this.fileName);
        fileWriter.write(this.data);
        fileWriter.close();
        System.out.println("Successfully wrote to the file.");
    } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
    }
}
    public void readFile(String args) {
    try {
        File fileobj = new File(args);
        Scanner fileReader = new Scanner(fileobj);
        while (fileReader.hasNextLine()){
            this.data = fileReader.nextLine();
            System.out.println(this.data);
        }
        fileReader.close();
    } catch (FileNotFoundException e) {
        System.out.println("An error occured");
        e.printStackTrace();
    }
    }
}
