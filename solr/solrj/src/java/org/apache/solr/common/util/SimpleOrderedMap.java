begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.common.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**<code>SimpleOrderedMap</code> is a {@link NamedList} where access by key is more  * important than maintaining order when it comes to representing the  * held data in other forms, as ResponseWriters normally do.  * It's normally not a good idea to repeat keys or use null keys, but this  * is not enforced.  If key uniqueness enforcement is desired, use a regular {@link Map}.  *<p>  * For example, a JSON response writer may choose to write a SimpleOrderedMap  * as {"foo":10,"bar":20} and may choose to write a NamedList as  * ["foo",10,"bar",20].  An XML response writer may choose to render both  * the same way.  *</p>  *<p>  * This class does not provide efficient lookup by key, its main purpose is  * to hold data to be serialized.  It aims to minimize overhead and to be  * efficient at adding new elements.  *</p>  */
end_comment

begin_class
DECL|class|SimpleOrderedMap
specifier|public
class|class
name|SimpleOrderedMap
parameter_list|<
name|T
parameter_list|>
extends|extends
name|NamedList
argument_list|<
name|T
argument_list|>
block|{
comment|/** Creates an empty instance */
DECL|method|SimpleOrderedMap
specifier|public
name|SimpleOrderedMap
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|SimpleOrderedMap
specifier|public
name|SimpleOrderedMap
parameter_list|(
name|int
name|sz
parameter_list|)
block|{
name|super
argument_list|(
name|sz
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an instance backed by an explicitly specified list of    * pairwise names/values.    *    *<p>    * TODO: this method was formerly public, now that it's not we can change the impl details of     * this class to be based on a Map.Entry[]     *</p>    *    * @param nameValuePairs underlying List which should be used to implement a SimpleOrderedMap; modifying this List will affect the SimpleOrderedMap.    * @lucene.internal    */
DECL|method|SimpleOrderedMap
specifier|private
name|SimpleOrderedMap
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|nameValuePairs
parameter_list|)
block|{
name|super
argument_list|(
name|nameValuePairs
argument_list|)
expr_stmt|;
block|}
DECL|method|SimpleOrderedMap
specifier|public
name|SimpleOrderedMap
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
index|[]
name|nameValuePairs
parameter_list|)
block|{
name|super
argument_list|(
name|nameValuePairs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|SimpleOrderedMap
argument_list|<
name|T
argument_list|>
name|clone
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|newList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|nvPairs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|newList
operator|.
name|addAll
argument_list|(
name|nvPairs
argument_list|)
expr_stmt|;
return|return
operator|new
name|SimpleOrderedMap
argument_list|<>
argument_list|(
name|newList
argument_list|)
return|;
block|}
block|}
end_class

end_unit

