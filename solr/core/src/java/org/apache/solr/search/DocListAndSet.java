begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * A struct whose only purpose is to hold both a {@link DocList} and a {@link DocSet}  * so that both may be returned from a single method.  *<p>  * The DocList and DocSet returned should<b>not</b> be modified as they may  * have been retrieved or inserted into a cache and should be considered shared.  *<p>  * Oh, if only java had "out" parameters or multiple return args...  *<p>  *  *  * @since solr 0.9  */
end_comment

begin_class
DECL|class|DocListAndSet
specifier|public
specifier|final
class|class
name|DocListAndSet
block|{
DECL|field|docList
specifier|public
name|DocList
name|docList
decl_stmt|;
DECL|field|docSet
specifier|public
name|DocSet
name|docSet
decl_stmt|;
block|}
end_class

end_unit

