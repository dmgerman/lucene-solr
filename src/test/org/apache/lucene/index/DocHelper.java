begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Created by IntelliJ IDEA.  * User: Grant Ingersoll  * Date: Feb 2, 2004  * Time: 6:16:12 PM  * $Id$  * Copyright 2004.  Center For Natural Language Processing  */
end_comment

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
name|WhitespaceAnalyzer
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
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|DocHelper
class|class
name|DocHelper
block|{
DECL|field|FIELD_1_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_1_TEXT
init|=
literal|"field one text"
decl_stmt|;
DECL|field|TEXT_FIELD_1_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TEXT_FIELD_1_KEY
init|=
literal|"textField1"
decl_stmt|;
DECL|field|textField1
specifier|public
specifier|static
name|Field
name|textField1
init|=
name|Field
operator|.
name|Text
argument_list|(
name|TEXT_FIELD_1_KEY
argument_list|,
name|FIELD_1_TEXT
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|FIELD_2_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_2_TEXT
init|=
literal|"field field field two text"
decl_stmt|;
comment|//Fields will be lexicographically sorted.  So, the order is: field, text, two
DECL|field|FIELD_2_FREQS
specifier|public
specifier|static
specifier|final
name|int
index|[]
name|FIELD_2_FREQS
init|=
block|{
literal|3
block|,
literal|1
block|,
literal|1
block|}
decl_stmt|;
DECL|field|TEXT_FIELD_2_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TEXT_FIELD_2_KEY
init|=
literal|"textField2"
decl_stmt|;
DECL|field|textField2
specifier|public
specifier|static
name|Field
name|textField2
init|=
operator|new
name|Field
argument_list|(
name|TEXT_FIELD_2_KEY
argument_list|,
name|FIELD_2_TEXT
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
decl_stmt|;
DECL|field|KEYWORD_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|KEYWORD_TEXT
init|=
literal|"Keyword"
decl_stmt|;
DECL|field|KEYWORD_FIELD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|KEYWORD_FIELD_KEY
init|=
literal|"keyField"
decl_stmt|;
DECL|field|keyField
specifier|public
specifier|static
name|Field
name|keyField
init|=
name|Field
operator|.
name|Keyword
argument_list|(
name|KEYWORD_FIELD_KEY
argument_list|,
name|KEYWORD_TEXT
argument_list|)
decl_stmt|;
DECL|field|UNINDEXED_FIELD_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|UNINDEXED_FIELD_TEXT
init|=
literal|"unindexed field text"
decl_stmt|;
DECL|field|UNINDEXED_FIELD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|UNINDEXED_FIELD_KEY
init|=
literal|"unIndField"
decl_stmt|;
DECL|field|unIndField
specifier|public
specifier|static
name|Field
name|unIndField
init|=
name|Field
operator|.
name|UnIndexed
argument_list|(
name|UNINDEXED_FIELD_KEY
argument_list|,
name|UNINDEXED_FIELD_TEXT
argument_list|)
decl_stmt|;
DECL|field|UNSTORED_1_FIELD_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|UNSTORED_1_FIELD_TEXT
init|=
literal|"unstored field text"
decl_stmt|;
DECL|field|UNSTORED_FIELD_1_KEY
specifier|public
specifier|static
specifier|final
name|String
name|UNSTORED_FIELD_1_KEY
init|=
literal|"unStoredField1"
decl_stmt|;
DECL|field|unStoredField1
specifier|public
specifier|static
name|Field
name|unStoredField1
init|=
name|Field
operator|.
name|UnStored
argument_list|(
name|UNSTORED_FIELD_1_KEY
argument_list|,
name|UNSTORED_1_FIELD_TEXT
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|UNSTORED_2_FIELD_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|UNSTORED_2_FIELD_TEXT
init|=
literal|"unstored field text"
decl_stmt|;
DECL|field|UNSTORED_FIELD_2_KEY
specifier|public
specifier|static
specifier|final
name|String
name|UNSTORED_FIELD_2_KEY
init|=
literal|"unStoredField2"
decl_stmt|;
DECL|field|unStoredField2
specifier|public
specifier|static
name|Field
name|unStoredField2
init|=
name|Field
operator|.
name|UnStored
argument_list|(
name|UNSTORED_FIELD_2_KEY
argument_list|,
name|UNSTORED_2_FIELD_TEXT
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//  public static Set fieldNamesSet = null;
comment|//  public static Set fieldValuesSet = null;
DECL|field|nameValues
specifier|public
specifier|static
name|Map
name|nameValues
init|=
literal|null
decl_stmt|;
static|static
block|{
name|nameValues
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|TEXT_FIELD_1_KEY
argument_list|,
name|FIELD_1_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|TEXT_FIELD_2_KEY
argument_list|,
name|FIELD_2_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|KEYWORD_FIELD_KEY
argument_list|,
name|KEYWORD_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|UNINDEXED_FIELD_KEY
argument_list|,
name|UNINDEXED_FIELD_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|UNSTORED_FIELD_1_KEY
argument_list|,
name|UNSTORED_1_FIELD_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|UNSTORED_FIELD_2_KEY
argument_list|,
name|UNSTORED_2_FIELD_TEXT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds the fields above to a document     * @param doc The document to write    */
DECL|method|setupDoc
specifier|public
specifier|static
name|void
name|setupDoc
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|textField1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|textField2
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|keyField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|unIndField
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|unStoredField1
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|unStoredField2
argument_list|)
expr_stmt|;
block|}
comment|/**    * Writes the document to the directory using a segment named "test"    * @param dir    * @param doc    */
DECL|method|writeDoc
specifier|public
specifier|static
name|void
name|writeDoc
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Document
name|doc
parameter_list|)
block|{
name|writeDoc
argument_list|(
name|dir
argument_list|,
literal|"test"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
comment|/**    * Writes the document to the directory in the given segment    * @param dir    * @param segment    * @param doc    */
DECL|method|writeDoc
specifier|public
specifier|static
name|void
name|writeDoc
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|String
name|segment
parameter_list|,
name|Document
name|doc
parameter_list|)
block|{
name|Analyzer
name|analyzer
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|()
decl_stmt|;
name|Similarity
name|similarity
init|=
name|Similarity
operator|.
name|getDefault
argument_list|()
decl_stmt|;
name|writeDoc
argument_list|(
name|dir
argument_list|,
name|analyzer
argument_list|,
name|similarity
argument_list|,
name|segment
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
comment|/**    * Writes the document to the directory segment named "test" using the specified analyzer and similarity    * @param dir    * @param analyzer    * @param similarity    * @param doc    */
DECL|method|writeDoc
specifier|public
specifier|static
name|void
name|writeDoc
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|Document
name|doc
parameter_list|)
block|{
name|writeDoc
argument_list|(
name|dir
argument_list|,
name|analyzer
argument_list|,
name|similarity
argument_list|,
literal|"test"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
comment|/**    * Writes the document to the directory segment using the analyzer and the similarity score    * @param dir    * @param analyzer    * @param similarity    * @param segment    * @param doc    */
DECL|method|writeDoc
specifier|public
specifier|static
name|void
name|writeDoc
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|String
name|segment
parameter_list|,
name|Document
name|doc
parameter_list|)
block|{
name|DocumentWriter
name|writer
init|=
operator|new
name|DocumentWriter
argument_list|(
name|dir
argument_list|,
name|analyzer
argument_list|,
name|similarity
argument_list|,
literal|50
argument_list|)
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|segment
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|numFields
specifier|public
specifier|static
name|int
name|numFields
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|Enumeration
name|fields
init|=
name|doc
operator|.
name|fields
argument_list|()
decl_stmt|;
name|int
name|result
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|fields
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|fields
operator|.
name|nextElement
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|result
operator|++
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

begin_comment
comment|/*     fieldNamesSet = new HashSet();     fieldNamesSet.add(TEXT_FIELD_1_KEY);     fieldNamesSet.add(TEXT_FIELD_2_KEY);     fieldNamesSet.add(KEYWORD_FIELD_KEY);     fieldNamesSet.add(UNINDEXED_FIELD_KEY);     fieldNamesSet.add(UNSTORED_FIELD_1_KEY);     fieldNamesSet.add(UNSTORED_FIELD_2_KEY);     fieldValuesSet = new HashSet();     fieldValuesSet.add(FIELD_1_TEXT);     fieldValuesSet.add(FIELD_2_TEXT);     fieldValuesSet.add(KEYWORD_TEXT);     fieldValuesSet.add(UNINDEXED_FIELD_TEXT);     fieldValuesSet.add(UNSTORED_1_FIELD_TEXT);     fieldValuesSet.add(UNSTORED_2_FIELD_TEXT); */
end_comment

end_unit

