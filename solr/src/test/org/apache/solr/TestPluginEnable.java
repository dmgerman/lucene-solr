begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|client
operator|.
name|solrj
operator|.
name|SolrServerException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  *<p> Test disabling components</p>  *  * @version $Id$  * @since solr 1.4  */
end_comment

begin_class
DECL|class|TestPluginEnable
specifier|public
class|class
name|TestPluginEnable
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-enableplugin.xml"
argument_list|,
literal|"schema-replication1.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|SolrServerException
block|{
name|assertNull
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"disabled"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getRequestHandler
argument_list|(
literal|"enabled"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

