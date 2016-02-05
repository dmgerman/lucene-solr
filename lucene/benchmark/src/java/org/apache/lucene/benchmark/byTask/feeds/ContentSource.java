begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
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

begin_comment
comment|/**  * Represents content from a specified source, such as TREC, Reuters etc. A  * {@link ContentSource} is responsible for creating {@link DocData} objects for  * its documents to be consumed by {@link DocMaker}. It also keeps track  * of various statistics, such as how many documents were generated, size in  * bytes etc.  *<p>  * For supported configuration parameters see {@link ContentItemsSource}.  */
end_comment

begin_class
DECL|class|ContentSource
specifier|public
specifier|abstract
class|class
name|ContentSource
extends|extends
name|ContentItemsSource
block|{
comment|/** Returns the next {@link DocData} from the content source.     * Implementations must account for multi-threading, as multiple threads     * can call this method simultaneously. */
DECL|method|getNextDocData
specifier|public
specifier|abstract
name|DocData
name|getNextDocData
parameter_list|(
name|DocData
name|docData
parameter_list|)
throws|throws
name|NoMoreDataException
throws|,
name|IOException
function_decl|;
block|}
end_class

end_unit

