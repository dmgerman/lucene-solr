begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

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
name|io
operator|.
name|Serializable
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
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_comment
comment|/**  * Test for<code>org.apache.solr.search.LRUCache</code>  */
end_comment

begin_class
DECL|class|TestLRUCache
specifier|public
class|class
name|TestLRUCache
extends|extends
name|TestCase
block|{
DECL|method|testFullAutowarm
specifier|public
name|void
name|testFullAutowarm
parameter_list|()
throws|throws
name|IOException
block|{
name|LRUCache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|lruCache
init|=
operator|new
name|LRUCache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"initialSize"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"autowarmCount"
argument_list|,
literal|"100%"
argument_list|)
expr_stmt|;
name|CacheRegenerator
name|cr
init|=
name|createCodeRegenerator
argument_list|()
decl_stmt|;
name|Object
name|o
init|=
name|lruCache
operator|.
name|init
argument_list|(
name|params
argument_list|,
literal|null
argument_list|,
name|cr
argument_list|)
decl_stmt|;
name|lruCache
operator|.
name|setState
argument_list|(
name|SolrCache
operator|.
name|State
operator|.
name|LIVE
argument_list|)
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
literal|101
condition|;
name|i
operator|++
control|)
block|{
name|lruCache
operator|.
name|put
argument_list|(
name|i
operator|+
literal|1
argument_list|,
literal|""
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"25"
argument_list|,
name|lruCache
operator|.
name|get
argument_list|(
literal|25
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|lruCache
operator|.
name|get
argument_list|(
literal|110
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|lruCache
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// first item put in should be the first out
name|LRUCache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|lruCacheNew
init|=
operator|new
name|LRUCache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|lruCacheNew
operator|.
name|init
argument_list|(
name|params
argument_list|,
name|o
argument_list|,
name|cr
argument_list|)
expr_stmt|;
name|lruCacheNew
operator|.
name|warm
argument_list|(
literal|null
argument_list|,
name|lruCache
argument_list|)
expr_stmt|;
name|lruCacheNew
operator|.
name|setState
argument_list|(
name|SolrCache
operator|.
name|State
operator|.
name|LIVE
argument_list|)
expr_stmt|;
name|lruCache
operator|.
name|close
argument_list|()
expr_stmt|;
name|lruCacheNew
operator|.
name|put
argument_list|(
literal|103
argument_list|,
literal|"103"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"90"
argument_list|,
name|lruCacheNew
operator|.
name|get
argument_list|(
literal|90
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"50"
argument_list|,
name|lruCacheNew
operator|.
name|get
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|lruCacheNew
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testPercentageAutowarm
specifier|public
name|void
name|testPercentageAutowarm
parameter_list|()
throws|throws
name|IOException
block|{
name|doTestPercentageAutowarm
argument_list|(
literal|100
argument_list|,
literal|50
argument_list|,
operator|new
name|int
index|[]
block|{
literal|51
block|,
literal|55
block|,
literal|60
block|,
literal|70
block|,
literal|80
block|,
literal|99
block|,
literal|100
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|5
block|,
literal|10
block|,
literal|20
block|,
literal|30
block|,
literal|40
block|,
literal|50
block|}
argument_list|)
expr_stmt|;
name|doTestPercentageAutowarm
argument_list|(
literal|100
argument_list|,
literal|25
argument_list|,
operator|new
name|int
index|[]
block|{
literal|76
block|,
literal|80
block|,
literal|99
block|,
literal|100
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|5
block|,
literal|10
block|,
literal|20
block|,
literal|30
block|,
literal|40
block|,
literal|50
block|,
literal|51
block|,
literal|55
block|,
literal|60
block|,
literal|70
block|}
argument_list|)
expr_stmt|;
name|doTestPercentageAutowarm
argument_list|(
literal|1000
argument_list|,
literal|10
argument_list|,
operator|new
name|int
index|[]
block|{
literal|901
block|,
literal|930
block|,
literal|950
block|,
literal|999
block|,
literal|1000
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|5
block|,
literal|100
block|,
literal|200
block|,
literal|300
block|,
literal|400
block|,
literal|800
block|,
literal|899
block|,
literal|900
block|}
argument_list|)
expr_stmt|;
name|doTestPercentageAutowarm
argument_list|(
literal|10
argument_list|,
literal|10
argument_list|,
operator|new
name|int
index|[]
block|{
literal|10
block|}
argument_list|,
operator|new
name|int
index|[]
block|{
literal|1
block|,
literal|5
block|,
literal|9
block|,
literal|100
block|,
literal|200
block|,
literal|300
block|,
literal|400
block|,
literal|800
block|,
literal|899
block|,
literal|900
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|doTestPercentageAutowarm
specifier|private
name|void
name|doTestPercentageAutowarm
parameter_list|(
name|int
name|limit
parameter_list|,
name|int
name|percentage
parameter_list|,
name|int
index|[]
name|hits
parameter_list|,
name|int
index|[]
name|misses
parameter_list|)
throws|throws
name|IOException
block|{
name|LRUCache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|lruCache
init|=
operator|new
name|LRUCache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|limit
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"initialSize"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"autowarmCount"
argument_list|,
name|percentage
operator|+
literal|"%"
argument_list|)
expr_stmt|;
name|CacheRegenerator
name|cr
init|=
name|createCodeRegenerator
argument_list|()
decl_stmt|;
name|Object
name|o
init|=
name|lruCache
operator|.
name|init
argument_list|(
name|params
argument_list|,
literal|null
argument_list|,
name|cr
argument_list|)
decl_stmt|;
name|lruCache
operator|.
name|setState
argument_list|(
name|SolrCache
operator|.
name|State
operator|.
name|LIVE
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|limit
condition|;
name|i
operator|++
control|)
block|{
name|lruCache
operator|.
name|put
argument_list|(
name|i
argument_list|,
literal|""
operator|+
name|i
argument_list|)
expr_stmt|;
comment|//adds numbers from 1 to 100
block|}
name|LRUCache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|lruCacheNew
init|=
operator|new
name|LRUCache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|lruCacheNew
operator|.
name|init
argument_list|(
name|params
argument_list|,
name|o
argument_list|,
name|cr
argument_list|)
expr_stmt|;
name|lruCacheNew
operator|.
name|warm
argument_list|(
literal|null
argument_list|,
name|lruCache
argument_list|)
expr_stmt|;
name|lruCacheNew
operator|.
name|setState
argument_list|(
name|SolrCache
operator|.
name|State
operator|.
name|LIVE
argument_list|)
expr_stmt|;
name|lruCache
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|hit
range|:
name|hits
control|)
block|{
name|assertEquals
argument_list|(
literal|"The value "
operator|+
name|hit
operator|+
literal|" should be on new cache"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|hit
argument_list|)
argument_list|,
name|lruCacheNew
operator|.
name|get
argument_list|(
name|hit
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|miss
range|:
name|misses
control|)
block|{
name|assertEquals
argument_list|(
literal|"The value "
operator|+
name|miss
operator|+
literal|" should NOT be on new cache"
argument_list|,
literal|null
argument_list|,
name|lruCacheNew
operator|.
name|get
argument_list|(
name|miss
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|lruCacheNew
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|testNoAutowarm
specifier|public
name|void
name|testNoAutowarm
parameter_list|()
throws|throws
name|IOException
block|{
name|LRUCache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|lruCache
init|=
operator|new
name|LRUCache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"initialSize"
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|CacheRegenerator
name|cr
init|=
name|createCodeRegenerator
argument_list|()
decl_stmt|;
name|Object
name|o
init|=
name|lruCache
operator|.
name|init
argument_list|(
name|params
argument_list|,
literal|null
argument_list|,
name|cr
argument_list|)
decl_stmt|;
name|lruCache
operator|.
name|setState
argument_list|(
name|SolrCache
operator|.
name|State
operator|.
name|LIVE
argument_list|)
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
literal|101
condition|;
name|i
operator|++
control|)
block|{
name|lruCache
operator|.
name|put
argument_list|(
name|i
operator|+
literal|1
argument_list|,
literal|""
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"25"
argument_list|,
name|lruCache
operator|.
name|get
argument_list|(
literal|25
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|lruCache
operator|.
name|get
argument_list|(
literal|110
argument_list|)
argument_list|)
expr_stmt|;
name|NamedList
argument_list|<
name|Serializable
argument_list|>
name|nl
init|=
name|lruCache
operator|.
name|getStatistics
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2L
argument_list|,
name|nl
operator|.
name|get
argument_list|(
literal|"lookups"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|nl
operator|.
name|get
argument_list|(
literal|"hits"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|101L
argument_list|,
name|nl
operator|.
name|get
argument_list|(
literal|"inserts"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|lruCache
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// first item put in should be the first out
name|LRUCache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|lruCacheNew
init|=
operator|new
name|LRUCache
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|lruCacheNew
operator|.
name|init
argument_list|(
name|params
argument_list|,
name|o
argument_list|,
name|cr
argument_list|)
expr_stmt|;
name|lruCacheNew
operator|.
name|warm
argument_list|(
literal|null
argument_list|,
name|lruCache
argument_list|)
expr_stmt|;
name|lruCacheNew
operator|.
name|setState
argument_list|(
name|SolrCache
operator|.
name|State
operator|.
name|LIVE
argument_list|)
expr_stmt|;
name|lruCache
operator|.
name|close
argument_list|()
expr_stmt|;
name|lruCacheNew
operator|.
name|put
argument_list|(
literal|103
argument_list|,
literal|"103"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|lruCacheNew
operator|.
name|get
argument_list|(
literal|90
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|lruCacheNew
operator|.
name|get
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|lruCacheNew
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|createCodeRegenerator
specifier|private
name|CacheRegenerator
name|createCodeRegenerator
parameter_list|()
block|{
name|CacheRegenerator
name|cr
init|=
operator|new
name|CacheRegenerator
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|boolean
name|regenerateItem
parameter_list|(
name|SolrIndexSearcher
name|newSearcher
parameter_list|,
name|SolrCache
name|newCache
parameter_list|,
name|SolrCache
name|oldCache
parameter_list|,
name|Object
name|oldKey
parameter_list|,
name|Object
name|oldVal
parameter_list|)
throws|throws
name|IOException
block|{
name|newCache
operator|.
name|put
argument_list|(
name|oldKey
argument_list|,
name|oldVal
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
return|return
name|cr
return|;
block|}
block|}
end_class

end_unit

