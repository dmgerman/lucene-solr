begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.de
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|de
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|LowerCaseFilter
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
name|analysis
operator|.
name|StopFilter
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|standard
operator|.
name|StandardFilter
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
name|analysis
operator|.
name|standard
operator|.
name|StandardTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_comment
comment|/**  * Analyzer for german language. Supports an external list of stopwords (words that  * will not be indexed at all) and an external list of exclusions (word that will  * not be stemmed, but indexed).  *  * @author    Gerhard Schwarz  * @version   $Id$  */
end_comment

begin_class
DECL|class|GermanAnalyzer
specifier|public
specifier|final
class|class
name|GermanAnalyzer
extends|extends
name|Analyzer
block|{
comment|/** 	 * List of typical german stopwords. 	 */
DECL|field|GERMAN_STOP_WORDS
specifier|private
name|String
index|[]
name|GERMAN_STOP_WORDS
init|=
block|{
literal|"einer"
block|,
literal|"eine"
block|,
literal|"eines"
block|,
literal|"einem"
block|,
literal|"einen"
block|,
literal|"der"
block|,
literal|"die"
block|,
literal|"das"
block|,
literal|"dass"
block|,
literal|"daß"
block|,
literal|"du"
block|,
literal|"er"
block|,
literal|"sie"
block|,
literal|"es"
block|,
literal|"was"
block|,
literal|"wer"
block|,
literal|"wie"
block|,
literal|"wir"
block|,
literal|"und"
block|,
literal|"oder"
block|,
literal|"ohne"
block|,
literal|"mit"
block|,
literal|"am"
block|,
literal|"im"
block|,
literal|"in"
block|,
literal|"aus"
block|,
literal|"auf"
block|,
literal|"ist"
block|,
literal|"sein"
block|,
literal|"war"
block|,
literal|"wird"
block|,
literal|"ihr"
block|,
literal|"ihre"
block|,
literal|"ihres"
block|,
literal|"als"
block|,
literal|"für"
block|,
literal|"von"
block|,
literal|"mit"
block|,
literal|"dich"
block|,
literal|"dir"
block|,
literal|"mich"
block|,
literal|"mir"
block|,
literal|"mein"
block|,
literal|"sein"
block|,
literal|"kein"
block|,
literal|"durch"
block|,
literal|"wegen"
block|}
decl_stmt|;
comment|/** 	 * Contains the stopwords used with the StopFilter. 	 */
DECL|field|stoptable
specifier|private
name|Hashtable
name|stoptable
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
comment|/** 	 * Contains words that should be indexed but not stemmed. 	 */
DECL|field|excltable
specifier|private
name|Hashtable
name|excltable
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
comment|/** 	 * Builds an analyzer. 	 */
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|()
block|{
name|stoptable
operator|=
name|StopFilter
operator|.
name|makeStopTable
argument_list|(
name|GERMAN_STOP_WORDS
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 */
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|(
name|String
index|[]
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
name|StopFilter
operator|.
name|makeStopTable
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 */
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|(
name|Hashtable
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
name|stopwords
expr_stmt|;
block|}
comment|/** 	 * Builds an analyzer with the given stop words. 	 */
DECL|method|GermanAnalyzer
specifier|public
name|GermanAnalyzer
parameter_list|(
name|File
name|stopwords
parameter_list|)
block|{
name|stoptable
operator|=
name|WordlistLoader
operator|.
name|getWordtable
argument_list|(
name|stopwords
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an exclusionlist from an array of Strings. 	 */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|String
index|[]
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|StopFilter
operator|.
name|makeStopTable
argument_list|(
name|exclusionlist
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Builds an exclusionlist from a Hashtable. 	 */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|Hashtable
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|exclusionlist
expr_stmt|;
block|}
comment|/** 	 * Builds an exclusionlist from the words contained in the given file. 	 */
DECL|method|setStemExclusionTable
specifier|public
name|void
name|setStemExclusionTable
parameter_list|(
name|File
name|exclusionlist
parameter_list|)
block|{
name|excltable
operator|=
name|WordlistLoader
operator|.
name|getWordtable
argument_list|(
name|exclusionlist
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Creates a TokenStream which tokenizes all the text in the provided Reader. 	 * 	 * @return  A TokenStream build from a StandardTokenizer filtered with 	 * 			StandardFilter, StopFilter, GermanStemFilter and LowerCaseFilter. 	 */
DECL|method|tokenStream
specifier|public
specifier|final
name|TokenStream
name|tokenStream
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
name|TokenStream
name|result
init|=
operator|new
name|StandardTokenizer
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|StandardFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|StopFilter
argument_list|(
name|result
argument_list|,
name|stoptable
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|GermanStemFilter
argument_list|(
name|result
argument_list|,
name|excltable
argument_list|)
expr_stmt|;
comment|// Convert to lowercase after stemming!
name|result
operator|=
operator|new
name|LowerCaseFilter
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

