package edu.umass.cs.cs646.features;

import edu.umass.cs.cs646.utils.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.*;
import java.util.*;

/**
 * Valar Dohaeris on 11/27/16.
 */
public class QLJMSmoothing{

    protected File dirBase;
    protected Directory dirLucene;
    public IndexReader index;
    protected Map<String, DocLengthReader> doclens;

    public QLJMSmoothing( String dirPath ) throws IOException {
        this( new File( dirPath ) );
    }

    public QLJMSmoothing( File dirBase ) throws IOException {
        this.dirBase = dirBase;
        this.dirLucene = FSDirectory.open( this.dirBase.toPath() );
        this.index = DirectoryReader.open( dirLucene );
        this.doclens = new HashMap<>();
        this.stopwords=new HashSet<>();
    }

    protected HashSet<String> stopwords;

    protected QLJMSmoothing() {
        this.stopwords = new HashSet<>();
    }

    public void setStopwords( Collection<String> stopwords ) {
        this.stopwords.addAll( stopwords );
    }

    public void setStopwords( String stopwordsPath ) throws IOException {
        setStopwords( new File( stopwordsPath ) );
    }

    public DocLengthReader getDocLengthReader( String field ) throws IOException {
        DocLengthReader doclen = doclens.get( field );
        if ( doclen == null ) {
            doclen = new FileDocLengthReader( this.dirBase, field );
            doclens.put( field, doclen );
        }
        return doclen;
    }

    public void setStopwords( File stopwordsFile ) throws IOException {
        BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( stopwordsFile ), "UTF-8" ) );
        String line;
        while ( ( line = reader.readLine() ) != null ) {
            line = line.trim();
            if ( line.length() > 0 ) {
                this.stopwords.add( line );
            }
        }
        reader.close();
    }

    public boolean isStopwords( String w ) {
        return stopwords.contains( w );
    }

    public List<SearchResult> search(String field, List<String> terms, int top, double lamdba, String stopWordsPath ) throws IOException {

        setStopwords(stopWordsPath);

        Map<String, Double> qfreqs = new TreeMap<>();
        for ( String term : terms ) {
            if ( !stopwords.contains( term ) ) {
                qfreqs.put( term, qfreqs.getOrDefault( term, 0.0 ) + 1 );
            }
        }

        List<PostingsEnum> postings = new ArrayList<>();
        List<Double> weights = new ArrayList<>();
        List<Double> tfcs = new ArrayList<>();
        for ( String term : qfreqs.keySet() ) {
            PostingsEnum list = MultiFields.getTermDocsEnum( index, field, new BytesRef( term ), PostingsEnum.FREQS );
            if ( list.nextDoc() != PostingsEnum.NO_MORE_DOCS ) {
                postings.add( list );
                weights.add( qfreqs.get( term ) / terms.size() );
                tfcs.add( 1.0 * index.totalTermFreq( new Term( field, term ) ) );
            }
        }
        return search( postings, weights, tfcs, getDocLengthReader( field ),
                index.getSumTotalTermFreq( field ),lamdba);
    }

    private List<SearchResult> search(List<PostingsEnum> postings, List<Double> weights, List<Double> tfcs,
                                      DocLengthReader doclen, double cl, double lamdba) throws IOException {

        PriorityQueue<SearchResult> topResults = new PriorityQueue<>( (r1, r2 ) -> {
            int cp = r1.getScore().compareTo( r2.getScore() );
            if ( cp == 0 ) {
                cp = r1.getDocid() - r2.getDocid();
            }
            return cp;
        } );

        List<Double> tfs = new ArrayList<>( weights.size() );
        for ( int ix = 0; ix < weights.size(); ix++ ) {
            tfs.add( 0.0 );
        }
        while ( true ) {

            int docid = Integer.MAX_VALUE;
            for ( PostingsEnum posting : postings ) {
                if ( posting.docID() != PostingsEnum.NO_MORE_DOCS && posting.docID() < docid ) {
                    docid = posting.docID();
                }
            }

            if ( docid == Integer.MAX_VALUE ) {
                break;
            }

            int ix = 0;
            for ( PostingsEnum posting : postings ) {
                if ( docid == posting.docID() ) {
                    tfs.set( ix, 1.0 * posting.freq() );
                    posting.nextDoc();
                } else {
                    tfs.set( ix, 0.0 );
                }
                ix++;
            }
            double score = score( lamdba, weights, tfs, tfcs, doclen.getLength( docid ), cl );

            topResults.add( new SearchResult( docid, null, score ) );
        }

        List<SearchResult> results = new ArrayList<>( topResults.size() );
        results.addAll( topResults );
        Collections.sort( results, ( o1, o2 ) -> o2.getScore().compareTo( o1.getScore() ) );
        return results;
    }


    public double score( Double lambda, List<Double> weights, List<Double> tfs, List<Double> tfcs, double dl, double cl ) {
            double result=0;

            for (int i=0;i<weights.size();i++)
            {
                double docStats=(1-lambda)*(tfs.get(i)/dl);
                double corpusStats=lambda*(tfcs.get(i)/cl);
                result+=weights.get(i)*Math.log(docStats+corpusStats);
            }

            return result;
    }
}
