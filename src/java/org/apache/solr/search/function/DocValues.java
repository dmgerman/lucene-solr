begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
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
name|Explanation
import|;
end_import

begin_comment
comment|/**  * Represents field values as different types.  * Normally created via a {@link ValueSource} for a particular field and reader.  *  * @author yonik  * @version $Id$  */
end_comment

begin_comment
comment|// DocValues is distinct from ValueSource because
end_comment

begin_comment
comment|// there needs to be an object created at query evaluation time that
end_comment

begin_comment
comment|// is not referenced by the query itself because:
end_comment

begin_comment
comment|// - Query objects should be MT safe
end_comment

begin_comment
comment|// - For caching, Query objects are often used as keys... you don't
end_comment

begin_comment
comment|//   want the Query carrying around big objects
end_comment

begin_class
DECL|class|DocValues
specifier|public
specifier|abstract
class|class
name|DocValues
block|{
DECL|method|floatVal
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|intVal
specifier|public
name|int
name|intVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|longVal
specifier|public
name|long
name|longVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|doubleVal
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|strVal
specifier|public
name|String
name|strVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|toString
specifier|public
specifier|abstract
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
operator|new
name|Explanation
argument_list|(
name|floatVal
argument_list|(
name|doc
argument_list|)
argument_list|,
name|toString
argument_list|(
name|doc
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

