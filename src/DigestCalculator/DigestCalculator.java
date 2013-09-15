package DigestCalculator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class DigestCalculator {
	
	public static void main( String[] args)
	{
		if (args.length < 3)
		{
			System.err.println("Numero de parametros incorreto, talvez você quis dizer:\n" +
					"$ java DigestCalculator <Tipo_Digest> <Caminho_ArqListaDigest> <Caminho_Arq1>[<Caminho_ArqN>]");
			System.exit(1);
		}   
    
    	String digestType = args[0];
    	ArrayList<String> ArqListFileNamesToProcess = getFilePathFromArqListToProcess(args);
    	
		HashMap<String,String> dictionaryOfDigests = parseDigestToProcessToDictionary(digestType, ArqListFileNamesToProcess);
		
		dumpDictionaryOfDigestsFromArqListToProcess(dictionaryOfDigests);
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
	
	private static ArrayList<BufferedReader> parseContentFromArqListaDigest(String[] args)
	{
		return null;
	}
	
	private static HashMap<String, String>  parseDigestToProcessToDictionary(String digestType, ArrayList<String> arqListToProcess)
	{
		HashMap<String, String> dictionaryOfDigestsFromArqListToProcess =
				new HashMap<String,String>();
		
		for(String filePath : arqListToProcess)
		{
			String input = calculateDigest(digestType, filePath);
			String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);

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
			System.err.println( "Arquivo " + filePath + " não pode ser lido." );
		}
		catch (java.security.NoSuchAlgorithmException e )
		{
			System.err.println( "Tipo_Digest " + digestType + " não é reconhecido." );
		}
		System.exit(1);
		return null;
	}
	
	/*
	 * Auxiliary Methods
	 */
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

	public static ArrayList<String> getDigestDictionary()
	{
		return new ArrayList<String>();
	}
	
	public static void dumpDictionaryOfDigestsFromArqListToProcess(HashMap<String,String> dictionaryOfDigestProcessed)
	{
		for (Entry<String, String> dictionaryOfDigests  : dictionaryOfDigestProcessed.entrySet())
		{
			System.out.println(dictionaryOfDigests.getKey());
			System.out.println();
			System.out.println(dictionaryOfDigests.getValue());
		}
	}
	
	private void tempFunc()
	{
	/*	String line = "";
		try{
			while((line = file.readLine())!= null)
			{
				String[] input = line.split(" ");
				for ( int i = 1 ; i < input.length ; i+=2 )
				{
					if (digestType != null || input[i].equals(digestType))
							dictionaryDigest.put(digestType, input[i+1]);
				}
			}
		}
		catch(IOException e)
		{
			System.err.println( "Arquivo não pode ser lido.");
			System.exit(1);
		}*/
		
	}
}
