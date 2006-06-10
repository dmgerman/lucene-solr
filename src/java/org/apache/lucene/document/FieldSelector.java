begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

begin_comment
comment|/**  * Created by IntelliJ IDEA.  * User: Grant Ingersoll  * Date: Apr 14, 2006  * Time: 5:29:26 PM  * $Id:$  * Copyright 2005.  Center For Natural Language Processing  */
end_comment

begin_comment
comment|/**  * Similar to a {@link java.io.FileFilter}, the FieldSelector allows one to make decisions about  * what Fields get loaded on a {@link Document} by {@link org.apache.lucene.index.IndexReader#document(int,org.apache.lucene.document.FieldSelector)}  *  **/
end_comment

begin_interface
DECL|interface|FieldSelector
specifier|public
interface|interface
name|FieldSelector
block|{
comment|/**    *     * @param fieldName    * @return true if the {@link Field} with<code>fieldName</code> should be loaded or not    */
DECL|method|accept
name|FieldSelectorResult
name|accept
parameter_list|(
name|String
name|fieldName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

