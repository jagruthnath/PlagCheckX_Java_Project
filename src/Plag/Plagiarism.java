package Plag;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.Math.pow;

public class Plagiarism {
    static ArrayList<String> fname = new ArrayList<String>();
    static ArrayList<String> fwp = new ArrayList<String>();
    static int kg=5;
    static String directory="",last="";
    static File[] files = new File(directory).listFiles();
    static int nof=0,size=0;
    static TreeMap<String,Integer> F1=new TreeMap<>();
    static TreeMap<String,Integer> F2=new TreeMap<>();
    static String html="<style>table {border-collapse: collapse;width:100%}th, td {padding: 12px;text-align: center;}</style>";
    void file_read()
    {
        files = new File(directory).listFiles();
        for (File file : files)
            if (file.isFile())
            {
                nof++;
                fname.add(file.getName());
                fwp.add(directory+file.getName());
            }
    }

    //Bag of Words methods start

    int dp()
    {
        int sum=0;
        for (Map.Entry<String , Integer> e1 : F1.entrySet())
        {
            String key1 = e1.getKey();
            Integer value1 = e1.getValue();
            for (Map.Entry<String , Integer> e2 : F2.entrySet())
            {
                String key2 = e2.getKey();
                Integer value2 = e2.getValue();
                if(key1.equals(key2))
                    sum+=(value1*value2);
        }
    }
        return sum;
    }

    float Euclid(TreeMap<String,Integer> File)
    {
        int sum=0;
        for (Map.Entry<String , Integer> e : File.entrySet())
        {
            Integer value = e.getValue();
            sum+= pow(value,2);
        }
        return (float)Math.sqrt(sum);
    }

    //Bag of Words methods end

    //Common method to store words for Bag of Words and LCS
    void process(String filename,TreeMap<String,Integer> F) throws IOException
    {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String result,content="";
            while ((result = reader.readLine())!=null)
                content=content.concat(result.toLowerCase());
            size=content.length();
            String[] words=content.split(" ");
            int i,count=0;
            for (i=0;i<words.length;i++)
            {
                count=0;
                for (int j=0;j<words.length;j++)
                    if(words[i].equals(words[j]))
                        count++;
                if(count!=0)
                        F.put(words[i],count);
            }
            reader.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not Found Exception.");
        }
    }

static int count=0;

    void print_matrix(float p[][])
    {
        if(count==1)
            html=html.concat("<h1 style='color:red;font-family:Quicksand Bold'> LCS </h1>");
        else if(count==2)
            html=html.concat("<h1 style='color:red;font-family:Quicksand Bold'> Fingerprinting </h1>");
        else
            html=html.concat("<h1 style='color:red;font-family:Quicksand Bold'> Bag of Words </h1>");
        count++;
        html=html+"<table border=1><tr><th style='color:red;font-family:Quicksand Bold;font-size:20'>FILE</th>";
        System.out.print("FILE\t");
        for(int i=0;i<nof;i++) {
            html=html.concat("<th style='color:red;font-family:Quicksand Bold;font-size:20'>"+fname.get(i)+"</th>");
            System.out.print(fname.get(i) + "\t");
        }
        html=html.concat("</tr>");
        System.out.println();
        for(int i=0;i<nof;i++)
        {
            html=html.concat("<tr><th style='color:red;font-family:Quicksand Bold;font-size:20'>"+fname.get(i)+"</th>");
            System.out.print(fname.get(i)+"\t");
            for(int j=0;j<nof;j++)
            {
                html=html.concat("<td style='font-family:Quicksand Book;font-size:18'>"+Float.toString(Math.round(p[i][j]))+"</td>");
                System.out.printf("%.2f\t",p[i][j]);
            }
            html=html.concat("</tr>");
            System.out.println();
        }
        html=html.concat("</table>");
    }

    //Fingerprinting methods start

    String process_finger(String filename) throws IOException
    {
        String result, content = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while ((result = reader.readLine()) != null)
                content = content.concat(result.toLowerCase());

            size = content.length();
            last=content.substring(size - kg);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not Found Exception.");
        }
        return content;
    }

    int generate_hash(String content,char last[],long ht[])
    {
        char content_temp[]=content.toCharArray();
        char hash[]=new char[kg];
        int j;
        long h=0;
        for (j = 0; !Arrays.equals(hash, last); j++)
        {
            int i;
            for (i = 0; i < kg ; i++)
                hash[i]=content_temp[j+i];
            for (int k = 0; k < kg; ++k)
                h+=(int)hash[k]*pow(10,k);
            h%=10007;
            ht[j]=h;
        }
        return j;
    }

    int generate_p(long hash_table1[],long hash_table2[],int THA,int THB,int small)
    {
        int c=0;
        for (int i = 0; i < small; ++i)
            if ( hash_table1[i] == hash_table2[i] )
                c++;
        return (2*c*100)/(THA+THB);
    }
    //Fingerprinting methods end

}