begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|lucene
operator|.
name|util
operator|.
name|InfoStream
import|;
end_import

begin_comment
comment|/**<p>Expert: {@link IndexWriter} uses an instance  *  implementing this interface to execute the merges  *  selected by a {@link MergePolicy}.  The default  *  MergeScheduler is {@link ConcurrentMergeScheduler}.</p>  *<p>Implementers of sub-classes should make sure that {@link #clone()}  *  returns an independent instance able to work with any {@link IndexWriter}  *  instance.</p>  * @lucene.experimental */
end_comment

begin_class
DECL|class|MergeScheduler
specifier|public
specifier|abstract
class|class
name|MergeScheduler
implements|implements
name|Closeable
block|{
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|MergeScheduler
specifier|protected
name|MergeScheduler
parameter_list|()
block|{   }
comment|/** Run the merges provided by {@link IndexWriter#getNextMerge()}.    * @param writer the {@link IndexWriter} to obtain the merges from.    * @param trigger the {@link MergeTrigger} that caused this merge to happen    * @param newMergesFound<code>true</code> iff any new merges were found by the caller otherwise<code>false</code>    * */
DECL|method|merge
specifier|public
specifier|abstract
name|void
name|merge
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|MergeTrigger
name|trigger
parameter_list|,
name|boolean
name|newMergesFound
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** Close this MergeScheduler. */
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** For messages about merge scheduling */
DECL|field|infoStream
specifier|protected
name|InfoStream
name|infoStream
decl_stmt|;
comment|/** IndexWriter calls this on init. */
DECL|method|setInfoStream
specifier|final
name|void
name|setInfoStream
parameter_list|(
name|InfoStream
name|infoStream
parameter_list|)
block|{
name|this
operator|.
name|infoStream
operator|=
name|infoStream
expr_stmt|;
block|}
comment|/**    * Returns true if infoStream messages are enabled. This method is usually used in    * conjunction with {@link #message(String)}:    *     *<pre class="prettyprint">    * if (verbose()) {    *   message(&quot;your message&quot;);    * }    *</pre>    */
DECL|method|verbose
specifier|protected
name|boolean
name|verbose
parameter_list|()
block|{
return|return
name|infoStream
operator|!=
literal|null
operator|&&
name|infoStream
operator|.
name|isEnabled
argument_list|(
literal|"MS"
argument_list|)
return|;
block|}
comment|/**    * Outputs the given message - this method assumes {@link #verbose()} was    * called and returned true.    */
DECL|method|message
specifier|protected
name|void
name|message
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|infoStream
operator|.
name|message
argument_list|(
literal|"MS"
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

