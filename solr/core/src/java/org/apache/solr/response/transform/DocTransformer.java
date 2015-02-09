begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.response.transform
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|response
operator|.
name|transform
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|SolrDocument
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
name|SolrQueryRequest
import|;
end_import

begin_comment
comment|/**  * A DocTransformer can add, remove or alter a Document before it is written out to the Response.  For instance, there are implementations  * that can put explanations inline with a document, add constant values and mark items as being artificially boosted (see {@link org.apache.solr.handler.component.QueryElevationComponent})  *  *<p>  * New instance for each request  *  * @see TransformerFactory  *  */
end_comment

begin_class
DECL|class|DocTransformer
specifier|public
specifier|abstract
class|class
name|DocTransformer
block|{
comment|/**    *    * @return The name of the transformer    */
DECL|method|getName
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * This is called before transform and sets    * @param context The {@link org.apache.solr.response.transform.TransformContext} stores information about the current state of things in Solr that may be    * useful for doing transformations.    */
DECL|method|setContext
specifier|public
name|void
name|setContext
parameter_list|(
name|TransformContext
name|context
parameter_list|)
block|{}
comment|/**    * This is where implementations do the actual work    *    *    * @param doc The document to alter    * @param docid The Lucene internal doc id    * @throws IOException If there is a low-level I/O error.    */
DECL|method|transform
specifier|public
specifier|abstract
name|void
name|transform
parameter_list|(
name|SolrDocument
name|doc
parameter_list|,
name|int
name|docid
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

