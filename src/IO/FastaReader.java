package IO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FastaReader {

    private String [] description;
    private String [] sequence;

    public FastaReader(String filename)
    {
        readSequenceFromFile(filename);

    }

    void readSequenceFromFile(String file)
    {
        List desc= new ArrayList();
        List seq = new ArrayList();
        try{
            BufferedReader in     = new BufferedReader( new FileReader( file ) );
            StringBuffer   buffer = new StringBuffer();
            String         line   = in.readLine();

            if( line == null )
                throw new IOException( file + " is an empty file" );

            if( line.charAt( 0 ) != '>' )
                throw new IOException( "First line of " + file + " should start with '>'" );
            else
                desc.add(line.replace(">",""));
            for( line = in.readLine().trim(); line != null; line = in.readLine() )
            {
                if( line.length()>0 && line.charAt( 0 ) == '>' )
                {
                    seq.add(buffer.toString());
                    buffer = new StringBuffer();
                    desc.add(line.replace(">",""));
                } else
                    buffer.append( line.trim() );
            }
            if( buffer.length() != 0 )
                seq.add(buffer.toString());
        }catch(IOException e)
        {
            System.out.println("Error when reading "+file);
            e.printStackTrace();
        }

        description = new String[desc.size()];
        sequence = new String[seq.size()];
        for (int i=0; i< seq.size(); i++)
        {
            String acc = (String) desc.get(i);
            description[i] = acc.split(" ")[0];
            sequence[i]=(String) seq.get(i);
        }

    }

    //return all description as List
    public List<String> getDescription(){return Arrays.asList(description);}

}
