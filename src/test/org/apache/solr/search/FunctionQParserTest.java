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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
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
name|params
operator|.
name|ModifiableSolrParams
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|request
operator|.
name|SolrQueryRequest
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
name|search
operator|.
name|function
operator|.
name|FunctionQuery
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
name|search
operator|.
name|function
operator|.
name|LiteralValueSource
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
name|search
operator|.
name|function
operator|.
name|ConstValueSource
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
name|search
operator|.
name|function
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
name|solr
operator|.
name|util
operator|.
name|AbstractSolrTestCase
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

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|FunctionQParserTest
specifier|public
class|class
name|FunctionQParserTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema11.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig-functionquery.xml"
return|;
block|}
DECL|method|getCoreName
specifier|public
name|String
name|getCoreName
parameter_list|()
block|{
return|return
literal|"basic"
return|;
block|}
DECL|method|testFunctionQParser
specifier|public
name|void
name|testFunctionQParser
parameter_list|()
throws|throws
name|Exception
block|{
name|ModifiableSolrParams
name|local
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
argument_list|,
literal|"_val_:'foo'"
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|)
decl_stmt|;
name|FunctionQParser
name|parser
decl_stmt|;
name|Query
name|query
decl_stmt|;
name|FunctionQuery
name|fq
decl_stmt|;
name|parser
operator|=
operator|new
name|FunctionQParser
argument_list|(
literal|"'foo'"
argument_list|,
name|local
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|query
operator|=
name|parser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"query is not a FunctionQuery"
argument_list|,
name|query
operator|instanceof
name|FunctionQuery
argument_list|)
expr_stmt|;
name|fq
operator|=
operator|(
name|FunctionQuery
operator|)
name|query
expr_stmt|;
name|assertTrue
argument_list|(
literal|"ValueSource is not a LiteralValueSource"
argument_list|,
name|fq
operator|.
name|getValueSource
argument_list|()
operator|instanceof
name|LiteralValueSource
argument_list|)
expr_stmt|;
name|parser
operator|=
operator|new
name|FunctionQParser
argument_list|(
literal|"1.5"
argument_list|,
name|local
argument_list|,
name|params
argument_list|,
name|req
argument_list|)
expr_stmt|;
name|query
operator|=
name|parser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"query is not a FunctionQuery"
argument_list|,
name|query
operator|instanceof
name|FunctionQuery
argument_list|)
expr_stmt|;
name|fq
operator|=
operator|(
name|FunctionQuery
operator|)
name|query
expr_stmt|;
name|assertTrue
argument_list|(
literal|"ValueSource is not a LiteralValueSource"
argument_list|,
name|fq
operator|.
name|getValueSource
argument_list|()
operator|instanceof
name|ConstValueSource
argument_list|)
expr_stmt|;
comment|//TODO: Add more tests here to test the parser
block|}
block|}
end_class

end_unit

