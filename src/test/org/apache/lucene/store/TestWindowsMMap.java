begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|StandardAnalyzer
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
name|index
operator|.
name|IndexWriter
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
name|IndexSearcher
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
name|FSDirectory
import|;
end_import

begin_class
DECL|class|TestWindowsMMap
specifier|public
class|class
name|TestWindowsMMap
extends|extends
name|TestCase
block|{
DECL|field|alphabet
specifier|private
specifier|final
specifier|static
name|String
name|alphabet
init|=
literal|"abcdefghijklmnopqrstuvwzyz"
decl_stmt|;
DECL|field|random
specifier|private
name|Random
name|random
decl_stmt|;
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|random
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"org.apache.lucene.FSDirectory.class"
argument_list|,
literal|"org.apache.lucene.store.MMapDirectory"
argument_list|)
expr_stmt|;
block|}
DECL|method|randomToken
specifier|private
name|String
name|randomToken
parameter_list|()
block|{
name|int
name|tl
init|=
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|7
argument_list|)
decl_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|cx
init|=
literal|0
init|;
name|cx
operator|<
name|tl
condition|;
name|cx
operator|++
control|)
block|{
name|int
name|c
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|25
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|alphabet
operator|.
name|substring
argument_list|(
name|c
argument_list|,
name|c
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|randomField
specifier|private
name|String
name|randomField
parameter_list|()
block|{
name|int
name|fl
init|=
literal|1
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|StringBuffer
name|fb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|fx
init|=
literal|0
init|;
name|fx
operator|<
name|fl
condition|;
name|fx
operator|++
control|)
block|{
name|fb
operator|.
name|append
argument_list|(
name|randomToken
argument_list|()
argument_list|)
expr_stmt|;
name|fb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
return|return
name|fb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|field|storePathname
specifier|private
specifier|final
specifier|static
name|String
name|storePathname
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"tempDir"
argument_list|)
argument_list|,
literal|"testLuceneMmap"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|method|testMmapIndex
specifier|public
name|void
name|testMmapIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|FSDirectory
name|storeDirectory
decl_stmt|;
name|storeDirectory
operator|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
name|storePathname
argument_list|)
expr_stmt|;
comment|// plan to add a set of useful stopwords, consider changing some of the
comment|// interior filters.
name|StandardAnalyzer
name|analyzer
init|=
operator|new
name|StandardAnalyzer
argument_list|(
operator|new
name|HashSet
argument_list|()
argument_list|)
decl_stmt|;
comment|// TODO: something about lock timeouts and leftover locks.
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|storeDirectory
argument_list|,
name|analyzer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|storePathname
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|dx
init|=
literal|0
init|;
name|dx
operator|<
literal|1000
condition|;
name|dx
operator|++
control|)
block|{
name|String
name|f
init|=
name|randomField
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"data"
argument_list|,
name|f
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
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|rmDir
argument_list|(
operator|new
name|File
argument_list|(
name|storePathname
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|rmDir
specifier|private
name|void
name|rmDir
parameter_list|(
name|File
name|dir
parameter_list|)
block|{
name|File
index|[]
name|files
init|=
name|dir
operator|.
name|listFiles
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|files
index|[
name|i
index|]
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

