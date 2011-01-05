begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.quality.utils
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
name|utils
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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
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
name|ScoreDoc
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
name|IndexSearcher
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
name|TopDocs
import|;
end_import

begin_comment
comment|/**  * Create a log ready for submission.  * Extend this class and override  * {@link #report(QualityQuery, TopDocs, String, Searcher)}  * to create different reports.   */
end_comment

begin_class
DECL|class|SubmissionReport
specifier|public
class|class
name|SubmissionReport
block|{
DECL|field|nf
specifier|private
name|NumberFormat
name|nf
decl_stmt|;
DECL|field|logger
specifier|private
name|PrintWriter
name|logger
decl_stmt|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
comment|/**    * Constructor for SubmissionReport.    * @param logger if null, no submission data is created.     * @param name name of this run.    */
DECL|method|SubmissionReport
specifier|public
name|SubmissionReport
parameter_list|(
name|PrintWriter
name|logger
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|nf
operator|=
name|NumberFormat
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|nf
operator|.
name|setMaximumFractionDigits
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|nf
operator|.
name|setMinimumFractionDigits
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
comment|/**    * Report a search result for a certain quality query.    * @param qq quality query for which the results are reported.    * @param td search results for the query.    * @param docNameField stored field used for fetching the result doc name.      * @param searcher index access for fetching doc name.    * @throws IOException in case of a problem.    */
DECL|method|report
specifier|public
name|void
name|report
parameter_list|(
name|QualityQuery
name|qq
parameter_list|,
name|TopDocs
name|td
parameter_list|,
name|String
name|docNameField
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|logger
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|ScoreDoc
name|sd
index|[]
init|=
name|td
operator|.
name|scoreDocs
decl_stmt|;
name|String
name|sep
init|=
literal|" \t "
decl_stmt|;
name|DocNameExtractor
name|xt
init|=
operator|new
name|DocNameExtractor
argument_list|(
name|docNameField
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sd
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|docName
init|=
name|xt
operator|.
name|docName
argument_list|(
name|searcher
argument_list|,
name|sd
index|[
name|i
index|]
operator|.
name|doc
argument_list|)
decl_stmt|;
name|logger
operator|.
name|println
argument_list|(
name|qq
operator|.
name|getQueryID
argument_list|()
operator|+
name|sep
operator|+
literal|"Q0"
operator|+
name|sep
operator|+
name|format
argument_list|(
name|docName
argument_list|,
literal|20
argument_list|)
operator|+
name|sep
operator|+
name|format
argument_list|(
literal|""
operator|+
name|i
argument_list|,
literal|7
argument_list|)
operator|+
name|sep
operator|+
name|nf
operator|.
name|format
argument_list|(
name|sd
index|[
name|i
index|]
operator|.
name|score
argument_list|)
operator|+
name|sep
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
block|{
if|if
condition|(
name|logger
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|padd
specifier|private
specifier|static
name|String
name|padd
init|=
literal|"                                    "
decl_stmt|;
DECL|method|format
specifier|private
name|String
name|format
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|minLen
parameter_list|)
block|{
name|s
operator|=
operator|(
name|s
operator|==
literal|null
condition|?
literal|""
else|:
name|s
operator|)
expr_stmt|;
name|int
name|n
init|=
name|Math
operator|.
name|max
argument_list|(
name|minLen
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|s
operator|+
name|padd
operator|)
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|n
argument_list|)
return|;
block|}
block|}
end_class

end_unit

