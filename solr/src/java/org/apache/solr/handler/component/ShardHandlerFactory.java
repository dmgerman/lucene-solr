begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package

begin_class
DECL|class|ShardHandlerFactory
specifier|public
specifier|abstract
class|class
name|ShardHandlerFactory
block|{
DECL|method|getShardHandler
specifier|public
specifier|abstract
name|ShardHandler
name|getShardHandler
parameter_list|()
function_decl|;
block|}
end_class

end_unit

