begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrRequestHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_comment
comment|/**  *  *  **/
end_comment

begin_class
DECL|class|TestXIncludeConfig
specifier|public
class|class
name|TestXIncludeConfig
extends|extends
name|AbstractSolrTestCase
block|{
DECL|field|supports
specifier|protected
name|boolean
name|supports
decl_stmt|;
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
comment|//public String getSolrConfigFile() { return "solrconfig.xml"; }
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig-xinclude.xml"
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
name|supports
operator|=
literal|true
expr_stmt|;
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
try|try
block|{
comment|//see whether it even makes sense to run this test
name|dbf
operator|.
name|setXIncludeAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dbf
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
name|supports
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|testXInclude
specifier|public
name|void
name|testXInclude
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Figure out whether this JVM supports XInclude anyway, if it doesn't then don't run this test????
comment|// TODO: figure out a better way to handle this.
if|if
condition|(
name|supports
operator|==
literal|true
condition|)
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|SolrRequestHandler
name|solrRequestHandler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"dismaxOldStyleDefaults"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Solr Req Handler is null"
argument_list|,
name|solrRequestHandler
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Didn't run testXInclude, because this XML DocumentBuilderFactory doesn't support it"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

