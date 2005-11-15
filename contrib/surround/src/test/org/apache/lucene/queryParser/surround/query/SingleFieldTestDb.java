begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.surround.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|surround
operator|.
name|query
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
name|store
operator|.
name|RAMDirectory
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
name|index
operator|.
name|IndexWriter
import|;
end_import

begin_class
DECL|class|SingleFieldTestDb
specifier|public
class|class
name|SingleFieldTestDb
block|{
DECL|field|db
specifier|private
name|Directory
name|db
decl_stmt|;
DECL|field|docs
specifier|private
name|String
index|[]
name|docs
decl_stmt|;
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
DECL|method|SingleFieldTestDb
specifier|public
name|SingleFieldTestDb
parameter_list|(
name|String
index|[]
name|documents
parameter_list|,
name|String
name|fName
parameter_list|)
block|{
try|try
block|{
name|db
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|docs
operator|=
name|documents
expr_stmt|;
name|fieldName
operator|=
name|fName
expr_stmt|;
name|Analyzer
name|analyzer
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|db
argument_list|,
name|analyzer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|docs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|Document
name|d
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|d
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|fieldName
argument_list|,
name|docs
index|[
name|j
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|io
operator|.
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|Error
argument_list|(
name|ioe
argument_list|)
throw|;
block|}
block|}
DECL|method|getDb
name|Directory
name|getDb
parameter_list|()
block|{
return|return
name|db
return|;
block|}
DECL|method|getDocs
name|String
index|[]
name|getDocs
parameter_list|()
block|{
return|return
name|docs
return|;
block|}
DECL|method|getFieldname
name|String
name|getFieldname
parameter_list|()
block|{
return|return
name|fieldName
return|;
block|}
block|}
end_class

end_unit

