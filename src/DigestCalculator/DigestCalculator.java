package DigestCalculator;
import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;

public class DigestCalculator {
	
	public static void main( String[] args)
	{
		/*if (args.length < 3)
		{
			System.err.println("Numero de parametros incorreto, talvez você quis dizer:\n" +
					"$ java DigestCalculator <Tipo_Digest> <Caminho_Arq1>[<Caminho_ArqN>] <Caminho_ArqListaDigest>");
			System.exit(1);
		}*/
		
		
		Scanner reader = new Scanner(System.in);  
        String text[]= new String[3];
        System.out.println ("Insert a string");
        text[0] = reader.next();
        text[1]= reader.next();
        text[2] = reader.next();
        reader.close();
        
    
    	//String digestType = args[0];
		ArrayList<BufferedReader> listFiles = readFilePath(text);
		HashMap<String, String>  dictionaryDigest = parseDigestToDictionary("MD5", listFiles);

		
		System.out.println(">>>>>>>>>>> Loop");
		for(String s : dictionaryDigest.keySet())
		{
			System.out.println(">>>>>>>>>>> ss");
			System.out.println(s);
		}

	}
	
	private static ArrayList<BufferedReader> readFilePath(String[] args)
	{
		ArrayList<BufferedReader> listFiles = new ArrayList<BufferedReader>();
		for(int i=2; i<args.length; i++)
		{	
			 BufferedReader file = createFile(args[i]);
			 listFiles.add(file);
		}
		return listFiles;
	}
	
	private static HashMap<String, String>  parseDigestToDictionary(String digestType, ArrayList<BufferedReader> listFiles)
	{
		HashMap<String, String> dictionaryDigest = new HashMap<String,String>();
		for(BufferedReader file : listFiles)
		{
			dictionaryDigest = addDigestToDictionary(digestType,file,dictionaryDigest);
		}
		
		return dictionaryDigest;
	}
	
	private static HashMap<String, String>  addDigestToDictionary(String digestType, BufferedReader file, HashMap<String, String> dictionaryDigest)
	{
		String line = "";
		try{
			while((line = file.readLine())!= null)
			{
				String[] input = line.split(" ");
				for ( int i = 1 ; i < input.length ; i+=2 )
				{
					if (digestType == null || input[i].equals(digestType))
							dictionaryDigest.put(input[0], input[i+1]);
				}
			}
		}
		catch(IOException e)
		{
			System.err.println( "Arquivo não pode ser lido.");
			System.exit(1);
		}
		
		return dictionaryDigest;
	}
	
	private static BufferedReader createFile(String filePath)
	{
		try{
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			return reader;
		}
		catch(Exception error)
		{
			System.err.println("ATENCAO: O arquivo " + filePath + " não foi encontrado.");
			System.exit(1);
		}
		return null;
	}

}
