begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.core.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|core
operator|.
name|config
package|;
end_package

begin_comment
comment|/**  * This interface should be implemented by classes that wants to listen for  * field configuration requests. The implementation receives a  * {@link FieldConfig} object and may add/change its attributes.  *   * @see FieldConfig  * @see QueryConfigHandler  */
end_comment

begin_interface
DECL|interface|FieldConfigListener
specifier|public
interface|interface
name|FieldConfigListener
block|{
comment|/**    * This method is called ever time a field configuration is requested.    *     * @param fieldConfig    *          the field configuration requested, should never be null    */
DECL|method|buildFieldConfig
name|void
name|buildFieldConfig
parameter_list|(
name|FieldConfig
name|fieldConfig
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

