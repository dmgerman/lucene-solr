begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
operator|.
name|Store
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|FloatDocValuesField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|LegacyFloatField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|SortedDocValuesField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|BinaryDocValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DocValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|FieldInvertState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReaderContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|NumericDocValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|RandomIndexWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|SlowCompositeReaderWrapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|SortedDocValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|similarities
operator|.
name|Similarity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_comment
comment|/**  * Demonstrates an application of the {@link DiversifiedTopDocsCollector} in  * assembling a collection of top results but without over-representation of any  * one source (in this case top-selling singles from the 60s without having them  * all be Beatles records...). Results are ranked by the number of weeks a  * single is top of the charts and de-duped by the artist name.  *   */
end_comment

begin_class
DECL|class|TestDiversifiedTopDocsCollector
specifier|public
class|class
name|TestDiversifiedTopDocsCollector
extends|extends
name|LuceneTestCase
block|{
DECL|method|testNonDiversifiedResults
specifier|public
name|void
name|testNonDiversifiedResults
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numberOfTracksOnCompilation
init|=
literal|10
decl_stmt|;
name|int
name|expectedMinNumOfBeatlesHits
init|=
literal|5
decl_stmt|;
name|TopDocs
name|res
init|=
name|searcher
operator|.
name|search
argument_list|(
name|getTestQuery
argument_list|()
argument_list|,
name|numberOfTracksOnCompilation
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|numberOfTracksOnCompilation
argument_list|,
name|res
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// due to randomization of segment merging in tests the exact number of Beatles hits
comment|// selected varies between 5 and 6 but we prove the point they are over-represented
comment|// in our result set using a standard search.
name|assertTrue
argument_list|(
name|getMaxNumRecordsPerArtist
argument_list|(
name|res
operator|.
name|scoreDocs
argument_list|)
operator|>=
name|expectedMinNumOfBeatlesHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testFirstPageDiversifiedResults
specifier|public
name|void
name|testFirstPageDiversifiedResults
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Using a diversified collector we can limit the results from
comment|// any one artist.
name|int
name|requiredMaxHitsPerArtist
init|=
literal|2
decl_stmt|;
name|int
name|numberOfTracksOnCompilation
init|=
literal|10
decl_stmt|;
name|DiversifiedTopDocsCollector
name|tdc
init|=
name|doDiversifiedSearch
argument_list|(
name|numberOfTracksOnCompilation
argument_list|,
name|requiredMaxHitsPerArtist
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|sd
init|=
name|tdc
operator|.
name|topDocs
argument_list|(
literal|0
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
name|numberOfTracksOnCompilation
argument_list|,
name|sd
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getMaxNumRecordsPerArtist
argument_list|(
name|sd
argument_list|)
operator|<=
name|requiredMaxHitsPerArtist
argument_list|)
expr_stmt|;
block|}
DECL|method|testSecondPageResults
specifier|public
name|void
name|testSecondPageResults
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numberOfTracksPerCompilation
init|=
literal|10
decl_stmt|;
name|int
name|numberOfCompilations
init|=
literal|2
decl_stmt|;
name|int
name|requiredMaxHitsPerArtist
init|=
literal|1
decl_stmt|;
comment|// Volume 2 of our hits compilation - start at position 10
name|DiversifiedTopDocsCollector
name|tdc
init|=
name|doDiversifiedSearch
argument_list|(
name|numberOfTracksPerCompilation
operator|*
name|numberOfCompilations
argument_list|,
name|requiredMaxHitsPerArtist
argument_list|)
decl_stmt|;
name|ScoreDoc
index|[]
name|volume2
init|=
name|tdc
operator|.
name|topDocs
argument_list|(
name|numberOfTracksPerCompilation
argument_list|,
name|numberOfTracksPerCompilation
argument_list|)
operator|.
name|scoreDocs
decl_stmt|;
name|assertEquals
argument_list|(
name|numberOfTracksPerCompilation
argument_list|,
name|volume2
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|getMaxNumRecordsPerArtist
argument_list|(
name|volume2
argument_list|)
operator|<=
name|requiredMaxHitsPerArtist
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidArguments
specifier|public
name|void
name|testInvalidArguments
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numResults
init|=
literal|5
decl_stmt|;
name|DiversifiedTopDocsCollector
name|tdc
init|=
name|doDiversifiedSearch
argument_list|(
name|numResults
argument_list|,
literal|15
argument_list|)
decl_stmt|;
comment|// start< 0
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// start> pq.size()
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
name|numResults
operator|+
literal|1
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// start == pq.size()
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
name|numResults
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// howMany< 0
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// howMany == 0
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tdc
operator|.
name|topDocs
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
operator|.
name|scoreDocs
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|// Diversifying collector that looks up de-dup keys using SortedDocValues
comment|// from a top-level Reader
DECL|class|DocValuesDiversifiedCollector
specifier|private
specifier|static
specifier|final
class|class
name|DocValuesDiversifiedCollector
extends|extends
name|DiversifiedTopDocsCollector
block|{
DECL|field|sdv
specifier|private
specifier|final
name|SortedDocValues
name|sdv
decl_stmt|;
DECL|method|DocValuesDiversifiedCollector
specifier|public
name|DocValuesDiversifiedCollector
parameter_list|(
name|int
name|size
parameter_list|,
name|int
name|maxHitsPerKey
parameter_list|,
name|SortedDocValues
name|sdv
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|,
name|maxHitsPerKey
argument_list|)
expr_stmt|;
name|this
operator|.
name|sdv
operator|=
name|sdv
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getKeys
specifier|protected
name|NumericDocValues
name|getKeys
parameter_list|(
specifier|final
name|LeafReaderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
comment|// Keys are always expressed as a long so we obtain the
comment|// ordinal for our String-based artist name here
return|return
name|sdv
operator|.
name|getOrd
argument_list|(
name|context
operator|.
name|docBase
operator|+
name|docID
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
comment|// Alternative, faster implementation for converting String keys to longs
comment|// but with the potential for hash collisions
DECL|class|HashedDocValuesDiversifiedCollector
specifier|private
specifier|static
specifier|final
class|class
name|HashedDocValuesDiversifiedCollector
extends|extends
name|DiversifiedTopDocsCollector
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|vals
specifier|private
name|BinaryDocValues
name|vals
decl_stmt|;
DECL|method|HashedDocValuesDiversifiedCollector
specifier|public
name|HashedDocValuesDiversifiedCollector
parameter_list|(
name|int
name|size
parameter_list|,
name|int
name|maxHitsPerKey
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|,
name|maxHitsPerKey
argument_list|)
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getKeys
specifier|protected
name|NumericDocValues
name|getKeys
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|vals
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|vals
operator|.
name|get
argument_list|(
name|docID
argument_list|)
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|vals
operator|=
name|DocValues
operator|.
name|getBinary
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|field
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|getLeafCollector
argument_list|(
name|context
argument_list|)
return|;
block|}
block|}
comment|// Test data - format is artist, song, weeks at top of charts
DECL|field|hitsOfThe60s
specifier|private
specifier|static
name|String
index|[]
name|hitsOfThe60s
init|=
block|{
literal|"1966\tSPENCER DAVIS GROUP\tKEEP ON RUNNING\t1"
block|,
literal|"1966\tOVERLANDERS\tMICHELLE\t3"
block|,
literal|"1966\tNANCY SINATRA\tTHESE BOOTS ARE MADE FOR WALKIN'\t4"
block|,
literal|"1966\tWALKER BROTHERS\tTHE SUN AIN'T GONNA SHINE ANYMORE\t4"
block|,
literal|"1966\tSPENCER DAVIS GROUP\tSOMEBODY HELP ME\t2"
block|,
literal|"1966\tDUSTY SPRINGFIELD\tYOU DON'T HAVE TO SAY YOU LOVE ME\t1"
block|,
literal|"1966\tMANFRED MANN\tPRETTY FLAMINGO\t3"
block|,
literal|"1966\tROLLING STONES\tPAINT IT, BLACK\t1"
block|,
literal|"1966\tFRANK SINATRA\tSTRANGERS IN THE NIGHT\t3"
block|,
literal|"1966\tBEATLES\tPAPERBACK WRITER\t5"
block|,
literal|"1966\tKINKS\tSUNNY AFTERNOON\t2"
block|,
literal|"1966\tGEORGIE FAME AND THE BLUE FLAMES\tGETAWAY\t1"
block|,
literal|"1966\tCHRIS FARLOWE\tOUT OF TIME\t1"
block|,
literal|"1966\tTROGGS\tWITH A GIRL LIKE YOU\t2"
block|,
literal|"1966\tBEATLES\tYELLOW SUBMARINE/ELEANOR RIGBY\t4"
block|,
literal|"1966\tSMALL FACES\tALL OR NOTHING\t1"
block|,
literal|"1966\tJIM REEVES\tDISTANT DRUMS\t5"
block|,
literal|"1966\tFOUR TOPS\tREACH OUT I'LL BE THERE\t3"
block|,
literal|"1966\tBEACH BOYS\tGOOD VIBRATIONS\t2"
block|,
literal|"1966\tTOM JONES\tGREEN GREEN GRASS OF HOME\t4"
block|,
literal|"1967\tMONKEES\tI'M A BELIEVER\t4"
block|,
literal|"1967\tPETULA CLARK\tTHIS IS MY SONG\t2"
block|,
literal|"1967\tENGELBERT HUMPERDINCK\tRELEASE ME\t4"
block|,
literal|"1967\tNANCY SINATRA AND FRANK SINATRA\tSOMETHIN' STUPID\t2"
block|,
literal|"1967\tSANDIE SHAW\tPUPPET ON A STRING\t3"
block|,
literal|"1967\tTREMELOES\tSILENCE IS GOLDEN\t3"
block|,
literal|"1967\tPROCOL HARUM\tA WHITER SHADE OF PALE\t4"
block|,
literal|"1967\tBEATLES\tALL YOU NEED IS LOVE\t7"
block|,
literal|"1967\tSCOTT MCKENZIE\tSAN FRANCISCO (BE SURE TO WEAR SOME FLOWERS INYOUR HAIR)\t4"
block|,
literal|"1967\tENGELBERT HUMPERDINCK\tTHE LAST WALTZ\t5"
block|,
literal|"1967\tBEE GEES\tMASSACHUSETTS (THE LIGHTS WENT OUT IN)\t4"
block|,
literal|"1967\tFOUNDATIONS\tBABY NOW THAT I'VE FOUND YOU\t2"
block|,
literal|"1967\tLONG JOHN BALDRY\tLET THE HEARTACHES BEGIN\t2"
block|,
literal|"1967\tBEATLES\tHELLO GOODBYE\t5"
block|,
literal|"1968\tGEORGIE FAME\tTHE BALLAD OF BONNIE AND CLYDE\t1"
block|,
literal|"1968\tLOVE AFFAIR\tEVERLASTING LOVE\t2"
block|,
literal|"1968\tMANFRED MANN\tMIGHTY QUINN\t2"
block|,
literal|"1968\tESTHER AND ABI OFARIM\tCINDERELLA ROCKEFELLA\t3"
block|,
literal|"1968\tDAVE DEE, DOZY, BEAKY, MICK AND TICH\tTHE LEGEND OF XANADU\t1"
block|,
literal|"1968\tBEATLES\tLADY MADONNA\t2"
block|,
literal|"1968\tCLIFF RICHARD\tCONGRATULATIONS\t2"
block|,
literal|"1968\tLOUIS ARMSTRONG\tWHAT A WONDERFUL WORLD/CABARET\t4"
block|,
literal|"1968\tGARRY PUCKETT AND THE UNION GAP\tYOUNG GIRL\t4"
block|,
literal|"1968\tROLLING STONES\tJUMPING JACK FLASH\t2"
block|,
literal|"1968\tEQUALS\tBABY COME BACK\t3"
block|,
literal|"1968\tDES O'CONNOR\tI PRETEND\t1"
block|,
literal|"1968\tTOMMY JAMES AND THE SHONDELLS\tMONY MONY\t2"
block|,
literal|"1968\tCRAZY WORLD OF ARTHUR BROWN\tFIRE!\t1"
block|,
literal|"1968\tTOMMY JAMES AND THE SHONDELLS\tMONY MONY\t1"
block|,
literal|"1968\tBEACH BOYS\tDO IT AGAIN\t1"
block|,
literal|"1968\tBEE GEES\tI'VE GOTTA GET A MESSAGE TO YOU\t1"
block|,
literal|"1968\tBEATLES\tHEY JUDE\t8"
block|,
literal|"1968\tMARY HOPKIN\tTHOSE WERE THE DAYS\t6"
block|,
literal|"1968\tJOE COCKER\tWITH A LITTLE HELP FROM MY FRIENDS\t1"
block|,
literal|"1968\tHUGO MONTENEGRO\tTHE GOOD THE BAD AND THE UGLY\t4"
block|,
literal|"1968\tSCAFFOLD\tLILY THE PINK\t3"
block|,
literal|"1969\tMARMALADE\tOB-LA-DI, OB-LA-DA\t1"
block|,
literal|"1969\tSCAFFOLD\tLILY THE PINK\t1"
block|,
literal|"1969\tMARMALADE\tOB-LA-DI, OB-LA-DA\t2"
block|,
literal|"1969\tFLEETWOOD MAC\tALBATROSS\t1"
block|,
literal|"1969\tMOVE\tBLACKBERRY WAY\t1"
block|,
literal|"1969\tAMEN CORNER\t(IF PARADISE IS) HALF AS NICE\t2"
block|,
literal|"1969\tPETER SARSTEDT\tWHERE DO YOU GO TO (MY LOVELY)\t4"
block|,
literal|"1969\tMARVIN GAYE\tI HEARD IT THROUGH THE GRAPEVINE\t3"
block|,
literal|"1969\tDESMOND DEKKER AND THE ACES\tTHE ISRAELITES\t1"
block|,
literal|"1969\tBEATLES\tGET BACK\t6"
block|,
literal|"1969\tTOMMY ROE\tDIZZY\t1"
block|,
literal|"1969\tBEATLES\tTHE BALLAD OF JOHN AND YOKO\t3"
block|,
literal|"1969\tTHUNDERCLAP NEWMAN\tSOMETHING IN THE AIR\t3"
block|,
literal|"1969\tROLLING STONES\tHONKY TONK WOMEN\t5"
block|,
literal|"1969\tZAGER AND EVANS\tIN THE YEAR 2525 (EXORDIUM AND TERMINUS)\t3"
block|,
literal|"1969\tCREEDENCE CLEARWATER REVIVAL\tBAD MOON RISING\t3"
block|,
literal|"1969\tJANE BIRKIN AND SERGE GAINSBOURG\tJE T'AIME... MOI NON PLUS\t1"
block|,
literal|"1969\tBOBBIE GENTRY\tI'LL NEVER FALL IN LOVE AGAIN\t1"
block|,
literal|"1969\tARCHIES\tSUGAR, SUGAR\t4"
block|}
decl_stmt|;
DECL|field|parsedRecords
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Record
argument_list|>
name|parsedRecords
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Record
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|dir
specifier|private
name|Directory
name|dir
decl_stmt|;
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|searcher
specifier|private
name|IndexSearcher
name|searcher
decl_stmt|;
DECL|field|artistDocValues
specifier|private
name|SortedDocValues
name|artistDocValues
decl_stmt|;
DECL|class|Record
specifier|static
class|class
name|Record
block|{
DECL|field|year
name|String
name|year
decl_stmt|;
DECL|field|artist
name|String
name|artist
decl_stmt|;
DECL|field|song
name|String
name|song
decl_stmt|;
DECL|field|weeks
name|float
name|weeks
decl_stmt|;
DECL|field|id
name|String
name|id
decl_stmt|;
DECL|method|Record
specifier|public
name|Record
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|year
parameter_list|,
name|String
name|artist
parameter_list|,
name|String
name|song
parameter_list|,
name|float
name|weeks
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|year
operator|=
name|year
expr_stmt|;
name|this
operator|.
name|artist
operator|=
name|artist
expr_stmt|;
name|this
operator|.
name|song
operator|=
name|song
expr_stmt|;
name|this
operator|.
name|weeks
operator|=
name|weeks
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Record [id="
operator|+
name|id
operator|+
literal|", artist="
operator|+
name|artist
operator|+
literal|", weeks="
operator|+
name|weeks
operator|+
literal|", year="
operator|+
name|year
operator|+
literal|", song="
operator|+
name|song
operator|+
literal|"]"
return|;
block|}
block|}
DECL|method|doDiversifiedSearch
specifier|private
name|DiversifiedTopDocsCollector
name|doDiversifiedSearch
parameter_list|(
name|int
name|numResults
parameter_list|,
name|int
name|maxResultsPerArtist
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Alternate between implementations used for key lookups
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// Faster key lookup but with potential for collisions on larger datasets
return|return
name|doFuzzyDiversifiedSearch
argument_list|(
name|numResults
argument_list|,
name|maxResultsPerArtist
argument_list|)
return|;
block|}
else|else
block|{
comment|// Slower key lookup but 100% accurate
return|return
name|doAccurateDiversifiedSearch
argument_list|(
name|numResults
argument_list|,
name|maxResultsPerArtist
argument_list|)
return|;
block|}
block|}
DECL|method|doFuzzyDiversifiedSearch
specifier|private
name|DiversifiedTopDocsCollector
name|doFuzzyDiversifiedSearch
parameter_list|(
name|int
name|numResults
parameter_list|,
name|int
name|maxResultsPerArtist
parameter_list|)
throws|throws
name|IOException
block|{
name|DiversifiedTopDocsCollector
name|tdc
init|=
operator|new
name|HashedDocValuesDiversifiedCollector
argument_list|(
name|numResults
argument_list|,
name|maxResultsPerArtist
argument_list|,
literal|"artist"
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|getTestQuery
argument_list|()
argument_list|,
name|tdc
argument_list|)
expr_stmt|;
return|return
name|tdc
return|;
block|}
DECL|method|doAccurateDiversifiedSearch
specifier|private
name|DiversifiedTopDocsCollector
name|doAccurateDiversifiedSearch
parameter_list|(
name|int
name|numResults
parameter_list|,
name|int
name|maxResultsPerArtist
parameter_list|)
throws|throws
name|IOException
block|{
name|DiversifiedTopDocsCollector
name|tdc
init|=
operator|new
name|DocValuesDiversifiedCollector
argument_list|(
name|numResults
argument_list|,
name|maxResultsPerArtist
argument_list|,
name|artistDocValues
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|getTestQuery
argument_list|()
argument_list|,
name|tdc
argument_list|)
expr_stmt|;
return|return
name|tdc
return|;
block|}
DECL|method|getTestQuery
specifier|private
name|Query
name|getTestQuery
parameter_list|()
block|{
name|BooleanQuery
operator|.
name|Builder
name|testQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|testQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"year"
argument_list|,
literal|"1966"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|testQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"year"
argument_list|,
literal|"1967"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|testQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"year"
argument_list|,
literal|"1968"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|testQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"year"
argument_list|,
literal|"1969"
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|testQuery
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|// populate an index with documents - artist, song and weeksAtNumberOne
name|dir
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|Field
name|yearField
init|=
name|newTextField
argument_list|(
literal|"year"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|SortedDocValuesField
name|artistField
init|=
operator|new
name|SortedDocValuesField
argument_list|(
literal|"artist"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|""
argument_list|)
argument_list|)
decl_stmt|;
name|Field
name|weeksAtNumberOneField
init|=
operator|new
name|FloatDocValuesField
argument_list|(
literal|"weeksAtNumberOne"
argument_list|,
literal|0.0F
argument_list|)
decl_stmt|;
name|Field
name|weeksStoredField
init|=
operator|new
name|LegacyFloatField
argument_list|(
literal|"weeks"
argument_list|,
literal|0.0F
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|Field
name|idField
init|=
name|newStringField
argument_list|(
literal|"id"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|Field
name|songField
init|=
name|newTextField
argument_list|(
literal|"song"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|Field
name|storedArtistField
init|=
name|newTextField
argument_list|(
literal|"artistName"
argument_list|,
literal|""
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|idField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|weeksAtNumberOneField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|storedArtistField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|songField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|weeksStoredField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|yearField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|artistField
argument_list|)
expr_stmt|;
name|parsedRecords
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hitsOfThe60s
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|cols
index|[]
init|=
name|hitsOfThe60s
index|[
name|i
index|]
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|)
decl_stmt|;
name|Record
name|record
init|=
operator|new
name|Record
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
name|cols
index|[
literal|0
index|]
argument_list|,
name|cols
index|[
literal|1
index|]
argument_list|,
name|cols
index|[
literal|2
index|]
argument_list|,
name|Float
operator|.
name|valueOf
argument_list|(
name|cols
index|[
literal|3
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|parsedRecords
operator|.
name|put
argument_list|(
name|record
operator|.
name|id
argument_list|,
name|record
argument_list|)
expr_stmt|;
name|idField
operator|.
name|setStringValue
argument_list|(
name|record
operator|.
name|id
argument_list|)
expr_stmt|;
name|yearField
operator|.
name|setStringValue
argument_list|(
name|record
operator|.
name|year
argument_list|)
expr_stmt|;
name|storedArtistField
operator|.
name|setStringValue
argument_list|(
name|record
operator|.
name|artist
argument_list|)
expr_stmt|;
name|artistField
operator|.
name|setBytesValue
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|record
operator|.
name|artist
argument_list|)
argument_list|)
expr_stmt|;
name|songField
operator|.
name|setStringValue
argument_list|(
name|record
operator|.
name|song
argument_list|)
expr_stmt|;
name|weeksStoredField
operator|.
name|setFloatValue
argument_list|(
name|record
operator|.
name|weeks
argument_list|)
expr_stmt|;
name|weeksAtNumberOneField
operator|.
name|setFloatValue
argument_list|(
name|record
operator|.
name|weeks
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|10
operator|==
literal|0
condition|)
block|{
comment|// Causes the creation of multiple segments for our test
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|LeafReader
name|ar
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|artistDocValues
operator|=
name|ar
operator|.
name|getSortedDocValues
argument_list|(
literal|"artist"
argument_list|)
expr_stmt|;
comment|// All searches sort by song popularity
specifier|final
name|Similarity
name|base
init|=
name|searcher
operator|.
name|getSimilarity
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|DocValueSimilarity
argument_list|(
name|base
argument_list|,
literal|"weeksAtNumberOne"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|getMaxNumRecordsPerArtist
specifier|private
name|int
name|getMaxNumRecordsPerArtist
parameter_list|(
name|ScoreDoc
index|[]
name|sd
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|artistCounts
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sd
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
name|sd
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|Record
name|record
init|=
name|parsedRecords
operator|.
name|get
argument_list|(
name|doc
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
decl_stmt|;
name|Integer
name|count
init|=
name|artistCounts
operator|.
name|get
argument_list|(
name|record
operator|.
name|artist
argument_list|)
decl_stmt|;
name|int
name|newCount
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|count
operator|!=
literal|null
condition|)
block|{
name|newCount
operator|=
name|count
operator|.
name|intValue
argument_list|()
operator|+
literal|1
expr_stmt|;
block|}
name|result
operator|=
name|Math
operator|.
name|max
argument_list|(
name|result
argument_list|,
name|newCount
argument_list|)
expr_stmt|;
name|artistCounts
operator|.
name|put
argument_list|(
name|record
operator|.
name|artist
argument_list|,
name|newCount
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Similarity that wraps another similarity and replaces the final score    * according to whats in a docvalues field.    *     * @lucene.experimental    */
DECL|class|DocValueSimilarity
specifier|static
class|class
name|DocValueSimilarity
extends|extends
name|Similarity
block|{
DECL|field|sim
specifier|private
specifier|final
name|Similarity
name|sim
decl_stmt|;
DECL|field|scoreValueField
specifier|private
specifier|final
name|String
name|scoreValueField
decl_stmt|;
DECL|method|DocValueSimilarity
specifier|public
name|DocValueSimilarity
parameter_list|(
name|Similarity
name|sim
parameter_list|,
name|String
name|scoreValueField
parameter_list|)
block|{
name|this
operator|.
name|sim
operator|=
name|sim
expr_stmt|;
name|this
operator|.
name|scoreValueField
operator|=
name|scoreValueField
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|computeNorm
specifier|public
name|long
name|computeNorm
parameter_list|(
name|FieldInvertState
name|state
parameter_list|)
block|{
return|return
name|sim
operator|.
name|computeNorm
argument_list|(
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|computeWeight
specifier|public
name|SimWeight
name|computeWeight
parameter_list|(
name|CollectionStatistics
name|collectionStats
parameter_list|,
name|TermStatistics
modifier|...
name|termStats
parameter_list|)
block|{
return|return
name|sim
operator|.
name|computeWeight
argument_list|(
name|collectionStats
argument_list|,
name|termStats
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|simScorer
specifier|public
name|SimScorer
name|simScorer
parameter_list|(
name|SimWeight
name|stats
parameter_list|,
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SimScorer
name|sub
init|=
name|sim
operator|.
name|simScorer
argument_list|(
name|stats
argument_list|,
name|context
argument_list|)
decl_stmt|;
specifier|final
name|NumericDocValues
name|values
init|=
name|DocValues
operator|.
name|getNumeric
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|scoreValueField
argument_list|)
decl_stmt|;
return|return
operator|new
name|SimScorer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|float
name|score
parameter_list|(
name|int
name|doc
parameter_list|,
name|float
name|freq
parameter_list|)
block|{
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
operator|(
name|int
operator|)
name|values
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|computeSlopFactor
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
return|return
name|sub
operator|.
name|computeSlopFactor
argument_list|(
name|distance
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|computePayloadFactor
parameter_list|(
name|int
name|doc
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|BytesRef
name|payload
parameter_list|)
block|{
return|return
name|sub
operator|.
name|computePayloadFactor
argument_list|(
name|doc
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|payload
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|,
name|Explanation
name|freq
parameter_list|)
block|{
return|return
name|Explanation
operator|.
name|match
argument_list|(
name|Float
operator|.
name|intBitsToFloat
argument_list|(
operator|(
name|int
operator|)
name|values
operator|.
name|get
argument_list|(
name|doc
argument_list|)
argument_list|)
argument_list|,
literal|"indexDocValue("
operator|+
name|scoreValueField
operator|+
literal|")"
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

