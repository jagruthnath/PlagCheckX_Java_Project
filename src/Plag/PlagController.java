package Plag;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class PlagController {
    public Button Browse,Analyse;
    public TextField filename;
    public WebView webView ;

    public void initialize() throws IOException
    {

    }
    public void fopen()
    {
        //Folder browser
        String rootPath="C:/";
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select folder to check for plagiarism.");
        File defaultDirectory = new File(rootPath);
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(Browse.getScene().getWindow());
        filename.appendText(rootPath+selectedDirectory.getName());
        Analyse.setDisable(false);
    }
    public void analyse() throws IOException
    {

        System.out.print(filename.getText()+"/");
        Plagiarism plagiarism=new Plagiarism();
        Plagiarism.directory=filename.getText()+"/";
        plagiarism.file_read();
        int nof=Plagiarism.nof;
        float plagx[][] = new  float[nof][nof];
        float plagx_lcs[][] = new  float[nof][nof];
        float plagx_finger[][] = new  float[nof][nof];
        String[] files=new String [Plagiarism.fwp.size()];

        int i=0;
        for (String s:Plagiarism.fwp)
            files[i++]=s;

        for (int a=0;a<Plagiarism.fwp.size();a++)
        {
            for (int b = a; b < Plagiarism.fwp.size(); b++)
            {
                if (a != b)
                {
                    plagiarism.process(files[a], Plagiarism.F1);
                    int lens1=Plagiarism.size;
                    plagiarism.process(files[b], Plagiarism.F2);
                    int lens2=Plagiarism.size;

                    //Bag of Words Start

                    float euc1 = plagiarism.Euclid(Plagiarism.F1);
                    float euc2 = plagiarism.Euclid(Plagiarism.F2);
                    float dot = (plagiarism.dp()) / (euc1 * euc2);
                    dot *= 100;
                    plagx[a][b] = plagx[b][a] = dot;

                    //Bag of Words End

                    //LCS Start

                    int match=0;
                    for (Map.Entry<String , Integer> e1 : Plagiarism.F1.entrySet())
                    {
                        String key1 = e1.getKey();
                        for (Map.Entry<String , Integer> e2 : Plagiarism.F2.entrySet())
                        {
                            String key2 = e2.getKey();
                            if(key1.equals(key2))
                                match=match<key1.length()?key1.length():match;
                        }
                    }

                    float value=((match*2)*100)/(lens1+lens2);

                    plagx_lcs[a][b]=value;
                    plagx_lcs[b][a]=value;
                    //LCS End

                    //Fingerprinting Start
                    String content1=plagiarism.process_finger(files[a]);
                    String last1=Plagiarism.last;
                    int size_a=Plagiarism.size;

                    String content2=plagiarism.process_finger(files[b]);
                    String last2=Plagiarism.last;
                    int size_b=Plagiarism.size;

                    int small=size_a>size_b?size_b:size_a;
                    int THA,THB;
                    long hash_table1[]=new long[1000];
                    long hash_table2[]=new long[1000];
                    THA=plagiarism.generate_hash(content1,last1.toCharArray(),hash_table1);
                    THB=plagiarism.generate_hash(content2,last2.toCharArray(),hash_table2);
                    plagx_finger[a][b]=plagx_finger[b][a]=plagiarism.generate_p(hash_table1,hash_table2,THA,THB,small);
                    if (plagx_finger[a][b]>100)
                        plagx_finger[a][b]=plagx_finger[b][a]=100;
                }
                else{
                    plagx[a][b] = plagx[b][a] = 100;
                    plagx_lcs[a][b] = plagx_lcs[b][a] = 100;
                    plagx_finger[a][b]=plagx_finger[b][a]=100;
                }
            }
        }

        System.out.println(" Bag of Words ");
        plagiarism.print_matrix(plagx);

        System.out.println(" LCS ");
        plagiarism.print_matrix(plagx_lcs);

        System.out.println(" Fingerprinting ");
        plagiarism.print_matrix(plagx_finger);

        WebEngine webengine = this.webView.getEngine();
        webengine.loadContent(Plagiarism.html,"text/html");
    }
}