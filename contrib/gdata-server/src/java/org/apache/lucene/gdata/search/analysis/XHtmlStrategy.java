begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.search.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|search
operator|.
name|analysis
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
name|gdata
operator|.
name|search
operator|.
name|config
operator|.
name|IndexSchemaField
import|;
end_import

begin_comment
comment|/**  * @author Simon Willnauer  * @see org.apache.lucene.gdata.search.analysis.TestHTMLStrategy  */
end_comment

begin_class
DECL|class|XHtmlStrategy
specifier|public
class|class
name|XHtmlStrategy
extends|extends
name|HTMLStrategy
block|{
comment|/**      * @param fieldConfig      */
DECL|method|XHtmlStrategy
specifier|public
name|XHtmlStrategy
parameter_list|(
name|IndexSchemaField
name|fieldConfig
parameter_list|)
block|{
name|super
argument_list|(
name|fieldConfig
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

