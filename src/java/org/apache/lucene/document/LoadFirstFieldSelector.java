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
comment|/**  * Created by IntelliJ IDEA.  * User: Grant Ingersoll  * Date: Apr 15, 2006  * Time: 10:13:07 AM  * $Id:$  * Copyright 2005.  Center For Natural Language Processing  */
end_comment

begin_comment
comment|/**  * Load the First field and break.  *<p/>  * See {@link FieldSelectorResult#LOAD_AND_BREAK}  */
end_comment

begin_class
DECL|class|LoadFirstFieldSelector
specifier|public
class|class
name|LoadFirstFieldSelector
implements|implements
name|FieldSelector
block|{
DECL|method|accept
specifier|public
name|FieldSelectorResult
name|accept
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|FieldSelectorResult
operator|.
name|LOAD_AND_BREAK
return|;
block|}
block|}
end_class

end_unit

