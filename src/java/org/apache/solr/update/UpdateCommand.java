begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
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
name|document
operator|.
name|Document
import|;
end_import

begin_comment
comment|/** An index update command encapsulated in an object (Command pattern)  *  * @author yonik  * @version $Id$  */
end_comment

begin_class
DECL|class|UpdateCommand
specifier|public
class|class
name|UpdateCommand
block|{
DECL|field|commandName
specifier|protected
name|String
name|commandName
decl_stmt|;
DECL|method|UpdateCommand
specifier|public
name|UpdateCommand
parameter_list|(
name|String
name|commandName
parameter_list|)
block|{
name|this
operator|.
name|commandName
operator|=
name|commandName
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|commandName
return|;
block|}
block|}
end_class

end_unit

