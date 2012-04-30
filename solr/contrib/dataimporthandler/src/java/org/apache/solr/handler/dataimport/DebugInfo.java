begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
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
name|StrUtils
import|;
end_import

begin_class
DECL|class|DebugInfo
specifier|public
class|class
name|DebugInfo
block|{
DECL|field|debugDocuments
specifier|public
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|debugDocuments
init|=
operator|new
name|ArrayList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|debugVerboseOutput
specifier|public
name|NamedList
argument_list|<
name|String
argument_list|>
name|debugVerboseOutput
init|=
literal|null
decl_stmt|;
DECL|field|verbose
specifier|public
name|boolean
name|verbose
decl_stmt|;
DECL|method|DebugInfo
specifier|public
name|DebugInfo
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|requestParams
parameter_list|)
block|{
name|verbose
operator|=
name|StrUtils
operator|.
name|parseBool
argument_list|(
operator|(
name|String
operator|)
name|requestParams
operator|.
name|get
argument_list|(
literal|"verbose"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|debugVerboseOutput
operator|=
operator|new
name|NamedList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

