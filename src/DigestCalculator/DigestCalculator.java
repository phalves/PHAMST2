package DigestCalculator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Alunos:
 * Anderson Moreira #07132843
 * Paulo Henrique C.Alves #0911325
 * 
 * */

public class DigestCalculator {
	
	public static void main( String[] args)
	{
		if (args.length < 3)
		{
			System.err.println("Numero de parametros incorreto, talvez voc? quis dizer:\n" +
					"$ java DigestCalculator <Tipo_Digest> <Caminho_ArqListaDigest> <Caminho_Arq1>[<Caminho_ArqN>]");
			System.exit(1);
		}   

    	String digestType = args[0];
    	String arqListDigestPath = args[1];
    	
    	ArrayList<String> ArqListFileNamesToProcess = getFilePathFromArqListToProcess(args);
    	
		HashMap<String,String> dictionaryOfDigests = parseDigestToProcessToDictionary(digestType, ArqListFileNamesToProcess);
		
    	HashMap<String,HashMap<String,String>> dictionaryFromListaDigest =
    			parseListaDigestToDictionary(arqListDigestPath);

    	compareDigest(dictionaryOfDigests, dictionaryFromListaDigest, digestType, arqListDigestPath);
    	
		//dumpDictionaryOfDigestsFromArqListToProcess(dictionaryOfDigests);
    	//dumpDictionaryOfDigestsFromListaDigest(dictionaryFromListaDigest); 
    	
    	
	}
	
	private static ArrayList<String> getFilePathFromArqListToProcess(String[] args)
	{
		ArrayList<String> listFiles = new ArrayList<String>();
		for(int i=2; i<args.length; i++)
		{	
			 listFiles.add(args[i]);
		}
		return listFiles;
	}
	
	private static HashMap<String,HashMap<String,String>> parseListaDigestToDictionary(String arqListDigestPath)
	{
		HashMap<String,HashMap<String,String>> dictionaryFromListaDigest =
				new HashMap<String,HashMap<String,String>>();
		try
		{
			BufferedReader reader = createFileReader(arqListDigestPath);
			String line = "";
			while((line = reader.readLine())!= null)
			{
				String[] input = line.split(" ");
				String fileName = input[0];
				
				HashMap<String,String> dictionaryDigest = 
						new HashMap<String,String>();
				
				for(int i = 1; i < input.length; i+=2)
				{
					dictionaryDigest.put(input[i], input[i+1]);
					dictionaryFromListaDigest.put(fileName, dictionaryDigest);
				}
			}
		}
		catch(Exception e)
		{
			System.err.println( "Arquivo n?o pode ser lido.");
			System.exit(1);
		}
		return dictionaryFromListaDigest;
	}
	
	private static HashMap<String, String>  parseDigestToProcessToDictionary(String digestType, ArrayList<String> arqListToProcess)
	{
		HashMap<String, String> dictionaryOfDigestsFromArqListToProcess =
				new HashMap<String,String>();
		
		for(String filePath : arqListToProcess)
		{
			String input = calculateDigest(digestType, filePath);
			String fileName = getFileName(filePath);
			dictionaryOfDigestsFromArqListToProcess.put(fileName, input);
		}
		
		return dictionaryOfDigestsFromArqListToProcess;
	}
	
	private static String calculateDigest(String digestType, String filePath)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance(digestType);
			digest.reset();
			
			File file = new File(filePath);
			byte[] fileData = Files.readAllBytes(Paths.get(file.toURI()));
			digest.update(fileData);
			
			byte[] digestData = digest.digest();
			
			StringBuilder digestString = new StringBuilder();
			for( byte b: digestData )
				digestString.append( String.format( "%02x", b&0xff ) );
			return digestString.toString();
		}
		catch ( IOException e )
		{
			System.err.println( "Arquivo " + filePath + " n?o pode ser lido." );
		}
		catch (java.security.NoSuchAlgorithmException e )
		{
			System.err.println( "Tipo_Digest " + digestType + " n?o ? reconhecido." );
		}
		System.exit(1);
		return null;
	}
	
	private static void compareDigest(HashMap<String,String> dictionaryOfDigests,
			HashMap<String,HashMap<String,String>> dictionaryFromListaDigest, String digestType, String arqListDigestPath )
	{
		for (Entry<String, String> dictionaryProcessed  : dictionaryOfDigests.entrySet())
		{
			String fileName = dictionaryProcessed.getKey();
			String digest = dictionaryProcessed.getValue();
			
			String status = isValidDigest(dictionaryFromListaDigest,fileName,digestType,digest);
			
			if(status.equals("(NOT FOUND)"))
			{
				System.out.println(fileName+" "+digestType+" "+digest+" "+status);
				appendDigestToFileBottom(fileName, digestType, digest, arqListDigestPath);
			}
			else{
				System.out.println(fileName+" "+digestType+" "+digest+" "+status);
			}
		}
	}
	
	private static void appendDigestToFileBottom(String fileName,
			String digestType, String digest, String arqListDigestPath) {
		try {
			FileWriter fw = new FileWriter(arqListDigestPath, true);
						
			BufferedWriter out = new BufferedWriter(fw);
			out.newLine();
			out.write(fileName +" "+ digestType +" "+ digest +"\n");
			out.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}

	private static String isValidDigest(HashMap<String,HashMap<String,String>> dictionaryFromListaDigest, 
			String fileName, String digestType, String digest)
	{
		for (Entry<String,HashMap<String,String>> dictionaryOfDigests  : dictionaryFromListaDigest.entrySet())
		{
			if(dictionaryOfDigests.getKey().equalsIgnoreCase(fileName))
			{
				String status = searchFileWithDigest(dictionaryOfDigests,digest,digestType);
				if(status.equals("(OK)") || status.equals("(NOT OK)") )
					return status;
			}
		}
		
		String status = searchColisionStatus(dictionaryFromListaDigest,fileName,digest);
		if(status.equals("(COLISION)"))
			return status;
		
		return "(NOT FOUND)";
	}

	private static String searchColisionStatus(HashMap<String,HashMap<String,String>> dictionaryFromListaDigest,
			String fileName, String digest)
	{
		for (Entry<String,HashMap<String,String>> dictionaryOfDigests  : dictionaryFromListaDigest.entrySet())
		{
			String arqName = dictionaryOfDigests.getKey();			
			for(Entry<String, String> dictionary : dictionaryOfDigests.getValue().entrySet())
			{				
				if(dictionary.getValue().equalsIgnoreCase(digest))
				{
					if(!arqName.equalsIgnoreCase(fileName))
					{
						return "(COLISION)";
					}
				}
			}
		}
		return "NOT_FOUND";
	}
	
	private static String searchFileWithDigest(Entry<String, HashMap<String, String>> dictionaryOfDigests,String digest,String digestType)
	{
		for(Entry<String, String> dictionary : dictionaryOfDigests.getValue().entrySet())
		{
			if(dictionary.getKey().equalsIgnoreCase(digestType))
			{
				if(dictionary.getValue().equalsIgnoreCase(digest))
				{
					return "(OK)";
				}
				else
				{
					return "(NOT OK)";
				}
			}
		}
		return "(NOT FOUND)";
	}
	
	public static String getFileName(String filePath) {

		int lastIndexOf = checkForUnixAndWindowsPath(filePath);

		if (lastIndexOf == -1) {
			return filePath;
		}

		String fileName = filePath.substring(lastIndexOf + 1, filePath.length());

		return fileName;
	}
	
	private static int checkForUnixAndWindowsPath(String filePath) {
		int lastIndexOf = filePath.lastIndexOf('/');

		if (lastIndexOf == -1) {
			lastIndexOf = filePath.lastIndexOf('\\');
			if (lastIndexOf == -1) 		
				return -1;
		}
		
		return lastIndexOf;
	}
	
	/*
	 * Auxiliary Methods
	 */
	private static BufferedReader createFileReader(String filePath)
	{
		try{
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			return reader;
		}
		catch(Exception error)
		{
			System.err.println("ATENCAO: O arquivo " + filePath + " n?o foi encontrado.");
			System.exit(1);
		}
		return null;
	}

	public static ArrayList<String> getDigestDictionary()
	{
		return new ArrayList<String>();
	}
	
	public static void dumpDictionaryOfDigestsFromArqListToProcess(HashMap<String,String> dictionaryOfDigestProcessed)
	{
		System.out.println("Digest gerados por arquivo:");
		for (Entry<String, String> dictionaryOfDigests  : dictionaryOfDigestProcessed.entrySet())
		{
			System.out.println(dictionaryOfDigests.getKey() + " " + dictionaryOfDigests.getValue());
			System.out.println();
		}
	}

	public static void dumpDictionaryOfDigestsFromListaDigest(HashMap<String,HashMap<String,String>> dictionaryFromListaDigest)
	{
		System.out.println("--------------");
		for (Entry<String,HashMap<String,String>> dictionaryOfDigests  : dictionaryFromListaDigest.entrySet())
		{
			System.out.println(dictionaryOfDigests.getKey());
			for(Entry<String, String> dictionary : dictionaryOfDigests.getValue().entrySet())
			{
				System.out.println(dictionary.getKey() + " " + dictionary.getValue());
			}
			System.out.println();
		}
	}
}
