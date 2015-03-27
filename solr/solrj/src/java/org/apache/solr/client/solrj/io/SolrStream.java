begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|client
operator|.
name|solrj
operator|.
name|impl
operator|.
name|HttpSolrClient
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
name|params
operator|.
name|ModifiableSolrParams
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
name|params
operator|.
name|SolrParams
import|;
end_import

begin_comment
comment|/** *  Queries a single Solr instance and maps SolrDocs to a Stream of Tuples. **/
end_comment

begin_class
DECL|class|SolrStream
specifier|public
class|class
name|SolrStream
extends|extends
name|TupleStream
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1
decl_stmt|;
DECL|field|baseUrl
specifier|private
name|String
name|baseUrl
decl_stmt|;
DECL|field|params
specifier|private
name|Map
name|params
decl_stmt|;
DECL|field|numWorkers
specifier|private
name|int
name|numWorkers
decl_stmt|;
DECL|field|workerID
specifier|private
name|int
name|workerID
decl_stmt|;
DECL|field|trace
specifier|private
name|boolean
name|trace
decl_stmt|;
DECL|field|fieldMappings
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fieldMappings
decl_stmt|;
DECL|field|jsonTupleStream
specifier|private
specifier|transient
name|JSONTupleStream
name|jsonTupleStream
decl_stmt|;
DECL|field|client
specifier|private
specifier|transient
name|HttpSolrClient
name|client
decl_stmt|;
DECL|field|cache
specifier|private
specifier|transient
name|SolrClientCache
name|cache
decl_stmt|;
DECL|method|SolrStream
specifier|public
name|SolrStream
parameter_list|(
name|String
name|baseUrl
parameter_list|,
name|Map
name|params
parameter_list|)
block|{
name|this
operator|.
name|baseUrl
operator|=
name|baseUrl
expr_stmt|;
name|this
operator|.
name|params
operator|=
name|params
expr_stmt|;
block|}
DECL|method|setFieldMappings
specifier|public
name|void
name|setFieldMappings
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fieldMappings
parameter_list|)
block|{
name|this
operator|.
name|fieldMappings
operator|=
name|fieldMappings
expr_stmt|;
block|}
DECL|method|children
specifier|public
name|List
argument_list|<
name|TupleStream
argument_list|>
name|children
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|()
return|;
block|}
DECL|method|getBaseUrl
specifier|public
name|String
name|getBaseUrl
parameter_list|()
block|{
return|return
name|baseUrl
return|;
block|}
DECL|method|setStreamContext
specifier|public
name|void
name|setStreamContext
parameter_list|(
name|StreamContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|numWorkers
operator|=
name|context
operator|.
name|numWorkers
expr_stmt|;
name|this
operator|.
name|workerID
operator|=
name|context
operator|.
name|workerID
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|context
operator|.
name|getSolrClientCache
argument_list|()
expr_stmt|;
block|}
comment|/**   * Opens the stream to a single Solr instance.   **/
DECL|method|open
specifier|public
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
name|client
operator|=
operator|new
name|HttpSolrClient
argument_list|(
name|baseUrl
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|client
operator|=
name|cache
operator|.
name|getHttpSolrClient
argument_list|(
name|baseUrl
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|jsonTupleStream
operator|=
name|JSONTupleStream
operator|.
name|create
argument_list|(
name|client
argument_list|,
name|loadParams
argument_list|(
name|params
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    *  Setting trace to true will include the "_CORE_" field in each Tuple emitted by the stream.    **/
DECL|method|setTrace
specifier|public
name|void
name|setTrace
parameter_list|(
name|boolean
name|trace
parameter_list|)
block|{
name|this
operator|.
name|trace
operator|=
name|trace
expr_stmt|;
block|}
DECL|method|loadParams
specifier|private
name|SolrParams
name|loadParams
parameter_list|(
name|Map
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|ModifiableSolrParams
name|solrParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|containsKey
argument_list|(
literal|"partitionKeys"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|params
operator|.
name|get
argument_list|(
literal|"partitionKeys"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"none"
argument_list|)
condition|)
block|{
name|String
name|partitionFilter
init|=
name|getPartitionFilter
argument_list|()
decl_stmt|;
name|solrParams
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|,
name|partitionFilter
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|numWorkers
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"When numWorkers> 1 partitionKeys must be set. Set partitionKeys=none to send the entire stream to each worker."
argument_list|)
throw|;
block|}
block|}
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|>
name|it
init|=
name|params
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|solrParams
operator|.
name|add
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|solrParams
return|;
block|}
DECL|method|getPartitionFilter
specifier|private
name|String
name|getPartitionFilter
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"{!hash workers="
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|this
operator|.
name|numWorkers
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" worker="
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|this
operator|.
name|workerID
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**   *  Closes the Stream to a single Solr Instance   * */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|jsonTupleStream
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**   * Reads a Tuple from the stream. The Stream is completed when Tuple.EOF == true.   **/
DECL|method|read
specifier|public
name|Tuple
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
name|fields
init|=
name|jsonTupleStream
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|trace
condition|)
block|{
name|fields
operator|.
name|put
argument_list|(
literal|"_CORE_"
argument_list|,
name|this
operator|.
name|baseUrl
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
comment|//Return the EOF tuple.
name|Map
name|m
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"EOF"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
operator|new
name|Tuple
argument_list|(
name|m
argument_list|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|fieldMappings
operator|!=
literal|null
condition|)
block|{
name|fields
operator|=
name|mapFields
argument_list|(
name|fields
argument_list|,
name|fieldMappings
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Tuple
argument_list|(
name|fields
argument_list|)
return|;
block|}
block|}
DECL|method|mapFields
specifier|private
name|Map
name|mapFields
parameter_list|(
name|Map
name|fields
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mappings
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|it
init|=
name|mappings
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|mapFrom
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|mapTo
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Object
name|o
init|=
name|fields
operator|.
name|get
argument_list|(
name|mapFrom
argument_list|)
decl_stmt|;
name|fields
operator|.
name|remove
argument_list|(
name|mapFrom
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|mapTo
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
return|return
name|fields
return|;
block|}
block|}
end_class

end_unit

