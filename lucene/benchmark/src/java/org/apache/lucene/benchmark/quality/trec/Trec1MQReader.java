begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.quality.trec
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|quality
operator|.
name|trec
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|benchmark
operator|.
name|quality
operator|.
name|QualityQuery
import|;
end_import

begin_comment
comment|/**  * Read topics of TREC 1MQ track.  *<p>  * Expects this topic format -  *<pre>  *   qnum:qtext  *</pre>  * Comment lines starting with '#' are ignored.  *<p>  * All topics will have a single name value pair.  */
end_comment

begin_class
DECL|class|Trec1MQReader
specifier|public
class|class
name|Trec1MQReader
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
comment|/**    *  Constructor for Trec's 1MQ TopicsReader    *  @param name name of name-value pair to set for all queries.    */
DECL|method|Trec1MQReader
specifier|public
name|Trec1MQReader
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**    * Read quality queries from trec 1MQ format topics file.    * @param reader where queries are read from.    * @return the result quality queries.    * @throws IOException if cannot read the queries.    */
DECL|method|readQueries
specifier|public
name|QualityQuery
index|[]
name|readQueries
parameter_list|(
name|BufferedReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|QualityQuery
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<
name|QualityQuery
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|line
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|null
operator|!=
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// id
name|int
name|k
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|k
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
comment|// qtext
name|String
name|qtext
init|=
name|line
operator|.
name|substring
argument_list|(
name|k
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
comment|// we got a topic!
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fields
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|qtext
argument_list|)
expr_stmt|;
comment|//System.out.println("id: "+id+" qtext: "+qtext+"  line: "+line);
name|QualityQuery
name|topic
init|=
operator|new
name|QualityQuery
argument_list|(
name|id
argument_list|,
name|fields
argument_list|)
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
name|topic
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// sort result array (by ID)
name|QualityQuery
name|qq
index|[]
init|=
name|res
operator|.
name|toArray
argument_list|(
operator|new
name|QualityQuery
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|qq
argument_list|)
expr_stmt|;
return|return
name|qq
return|;
block|}
block|}
end_class

end_unit

