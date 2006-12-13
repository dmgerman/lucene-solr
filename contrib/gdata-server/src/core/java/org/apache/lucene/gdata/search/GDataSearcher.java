begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.search
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

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
name|Query
import|;
end_import

begin_comment
comment|/**  * @author Simon Willnauer  * @param<T>   *  */
end_comment

begin_interface
DECL|interface|GDataSearcher
specifier|public
interface|interface
name|GDataSearcher
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**      * executes an Query and returns a list of defined return values of type T      * @param query - the query to apply to the searcher      * @param hitcount - the amount of hits returned by this search      * @param offset - the hit count offset       * @param feedId       * @return List of T      * @throws IOException - if the underlying lucene searcher throws an IO Exception       */
DECL|method|search
specifier|public
name|List
argument_list|<
name|T
argument_list|>
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
name|hitcount
parameter_list|,
name|int
name|offset
parameter_list|,
name|String
name|feedId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Destroys this Searcher      */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

