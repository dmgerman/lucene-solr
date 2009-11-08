begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
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
name|utils
operator|.
name|Config
import|;
end_import

begin_comment
comment|/**  * A DocMaker reading one line at a time as a Document from a single file. This  * saves IO cost (over DirContentSource) of recursing through a directory and  * opening a new file for every document. It also re-uses its Document and Field  * instance to improve indexing speed.<br>  * The expected format of each line is (arguments are separated by&lt;TAB&gt;):  *<i>title, date, body</i>. If a line is read in a different format, a  * {@link RuntimeException} will be thrown. In general, you should use this doc  * maker with files that were created with   * {@link org.apache.lucene.benchmark.byTask.tasks.WriteLineDocTask}.<br>  *<br>  * Config properties:  *<ul>  *<li>doc.random.id.limit=N (default -1) -- create random docid in the range  * 0..N; this is useful with UpdateDoc to test updating random documents; if  * this is unspecified or -1, then docid is sequentially assigned  *</ul>  * @deprecated Please use {@link DocMaker} instead, with content.source=LineDocSource  */
end_comment

begin_class
DECL|class|LineDocMaker
specifier|public
class|class
name|LineDocMaker
extends|extends
name|DocMaker
block|{
annotation|@
name|Override
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|source
operator|=
operator|new
name|LineDocSource
argument_list|()
expr_stmt|;
name|source
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NOTE: LineDocMaker is deprecated; please use DocMaker instead (which is the default if you don't specify doc.maker) with content.source=LineDocSource"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

