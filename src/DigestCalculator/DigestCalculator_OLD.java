package DigestCalculator;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Luis Henrique Pelosi e Monique Lima
 *
 */

public class DigestCalculator_OLD
{

	public static void main( String[] args )
	{
		// Confere formato dos argumentos de entrada
		if ( args.length < 3 )
		{
			System.err.println( "Como Usar: java DigestCalculator \"Tipo_Digest\" \"Caminho_Arq1\" \"Caminho_Arq2\" ... \"Caminho_ArqN\" \"Caminho_ArqListaDigest\"" );
			System.exit( 1 );
		}
		
		// Extrai os parametros
		List<String> paramList = Arrays.asList( args );
		String digestType = paramList.get( 0 );
		List<String> inputFileNames = paramList.subList( 1, paramList.size()-1 );
		String digestListFileName = paramList.get( paramList.size()-1 );
		
		HashMap< String, String > digestMap = getDigestMap( digestType, new File( digestListFileName ) );
		for ( String inputFileName: inputFileNames )
		{
			String computedDigest = computeDigest( digestType, new File( inputFileName ) );
			System.out.print( new File( inputFileName ).getName() + " " + digestType + " " + computedDigest + " " );
			inputFileName = new File( inputFileName ).getName();
			
			if ( digestMap.containsKey( inputFileName ) && digestMap.get( inputFileName ).equals( computedDigest ) )
			{
				System.out.println( "(OK)" );
			}
			else if ( digestMap.containsValue( computedDigest ) )
			{
				System.out.println( "(COLISION)" );
			}
			else if ( digestMap.containsKey( inputFileName ) )
			{
				System.out.println( "(NOT OK)" );
			}
			else
			{
				System.out.println( "(NOT FOUND)" );
				writeNewDigest( digestListFileName, inputFileName, digestType, computedDigest );
			}
		}
	}
	
	// Calcula o digest de um arquivo em string hexa
	public static String computeDigest( String digestType, File inputFile )
	{
		if( !inputFile.isFile( ) ) 
		{
			System.err.println( "Arquivo " + inputFile.getName() + " não foi encontrado." );
			System.exit(1);
		}
		try
		{
			// Instancia um digest de tipo especifico (MD5/SHA1)
			MessageDigest digest = MessageDigest.getInstance( digestType );
			digest.reset();
			// Alimenta com os bytes do arquivo de entrada
			byte[] fileData = Files.readAllBytes( Paths.get( inputFile.toURI() ) );
			digest.update( fileData );
			// Faz o calculo
			byte[] digestData = digest.digest();
			// Converte para hexa
			StringBuilder digestString = new StringBuilder();
			for( byte b: digestData )
				digestString.append( String.format( "%02x", b&0xff ) );
			return digestString.toString();
		}
		catch ( IOException e )
		{
			System.err.println( "Arquivo " + inputFile.getName() + " não pode ser lido." );
		}
		catch (java.security.NoSuchAlgorithmException e )
		{
			System.err.println( "Tipo_Digest " + digestType + " não é reconhecido." );
		}
		System.exit(1);
		return null; // Se deu algum problema
	}
	
	// Mapeamento ( arquivo -> digest )
	// Use digestType = null para uma lista extensiva de arquivos, independentemente do tipo
	private static HashMap< String, String > getDigestMap( String digestType, File inputFile )
	{
		try
		{
			BufferedReader reader = new BufferedReader( new FileReader( inputFile ) );
			HashMap< String, String > digestMap = new HashMap< String, String >();
			String inputLine;
			
			while ( ( inputLine = reader.readLine() ) != null )
			{
				String[] input = inputLine.split(" ");
				for ( int i = 1 ; i < input.length ; i+=2 )
				{
					if ( digestType == null || input[i].equals( digestType ) )
					{
						digestMap.put( input[0], input[i+1] );
					}
				}
			}
			
			reader.close();
			return digestMap;
		}
		catch ( IOException e )
		{
			System.err.println( "Arquivo " + inputFile.getName() + " não pode ser lido." );
		}
		System.exit(1);
		return null; // Se deu algum problema
	}
	
	private static void writeNewDigest( String listFile, String inputFile, String digestType, String digest )
	{
		HashMap< String, String > digestMap = getDigestMap( null, new File( listFile ) );
		// Caso o arquivo ja esteja na lista, acrescenta na mesma linha dele
		if ( digestMap.containsKey( new File( inputFile ).getName() ) )
		{
			try
			{
				List< String > lines = new ArrayList< String >();
				
				BufferedReader reader = new BufferedReader( new FileReader( listFile ) );
				while ( reader.ready() )
				{
					String line = reader.readLine();
					if ( line.startsWith( new File( inputFile ).getName() ) )
					{
						line = line.concat( " " + digestType + " " + digest );
					}
					lines.add( line );
				}
				reader.close();
				
				FileWriter writer = new FileWriter( new File( listFile ), false );
				for( String line: lines )
				{
					writer.write( line + System.getProperty("line.separator") );
				}
				writer.close();
			}
			catch ( IOException e )
			{
				System.err.println( "Arquivo " + listFile + " não pode ser escrito." );
				System.exit(1);
			}
		}
		// Se nao tiver, poe uma linha nova no final
		else
		{
			try
			{
				FileWriter fWriter = new FileWriter( listFile, true );
				BufferedWriter writer = new BufferedWriter( fWriter );
				writer.append( new File( inputFile ).getName() + " " + digestType + " " + digest );
				writer.newLine();
				writer.close();
			}
			catch ( IOException e )
			{
				System.err.println( "Arquivo " + listFile + " não pode ser escrito." );
				System.exit(1);
			}
		}
	}
}
