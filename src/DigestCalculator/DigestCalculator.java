package DigestCalculator;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Dictionary;

public class DigestCalculator {
	
	public void main( String[] args)
	{
		if ( args.length < 3 )
		{
			System.err.println("Numero de parametros incorreto, talvez você quis dizer:\n" +
					"$ java DigestCalculator <Tipo_Digest> <Caminho_Arq1>[<Caminho_ArqN>] <Caminho_ArqListaDigest>");
			System.exit(1);
		}
		readFilePath(args);
	}
	
	private Dictionary<String, String> readFilePath(String[] args)
	{
		 ArrayList<BufferedReader> listFiles = new ArrayList<BufferedReader>();
		 Dictionary<String, String> digestDictionary;

		for(int i=2; i<args.length; i++)
		{	
			 BufferedReader file = createFile(args[i]);
			 listFiles.add(file);
		}
		
		return null;
	}
	
	private BufferedReader createFile(String filePath)
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
