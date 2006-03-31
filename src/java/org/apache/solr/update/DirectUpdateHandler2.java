begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * @author yonik  */
end_comment

begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|Term
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|TermDocs
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
name|document
operator|.
name|Document
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
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
name|net
operator|.
name|URL
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
name|search
operator|.
name|SolrIndexSearcher
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
name|search
operator|.
name|QueryParsing
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
name|util
operator|.
name|NamedList
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
name|core
operator|.
name|SolrCore
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
name|core
operator|.
name|SolrException
import|;
end_import

begin_comment
comment|/**  *<code>DirectUpdateHandler2</code> implements an UpdateHandler where documents are added  * directly to the main Lucene index as opposed to adding to a separate smaller index.  * For this reason, not all combinations to/from pending and committed are supported.  * This version supports efficient removal of duplicates on a commit.  It works by maintaining  * a related count for every document being added or deleted.  At commit time, for every id with a count,  * all but the last "count" docs with that id are deleted.  *<p>  *  * Supported add command parameters:<TABLE BORDER><TR><TH>allowDups</TH><TH>overwritePending</TH><TH>overwriteCommitted</TH><TH>efficiency</TH></TR><TR><TD>false</TD><TD>false</TD><TD>true</TD><TD>fast</TD></TR><TR><TD>true or false</TD><TD>true</TD><TD>true</TD><TD>fast</TD></TR><TR><TD>true</TD><TD>false</TD><TD>false</TD><TD>fastest</TD></TR></TABLE><p>Supported delete commands:<TABLE BORDER><TR><TH>command</TH><TH>fromPending</TH><TH>fromCommitted</TH><TH>efficiency</TH></TR><TR><TD>delete</TD><TD>true</TD><TD>true</TD><TD>fast</TD></TR><TR><TD>deleteByQuery</TD><TD>true</TD><TD>true</TD><TD>very slow*</TD></TR></TABLE><p>* deleteByQuery causes a commit to happen (close current index writer, open new index reader)   before it can be processed.  If deleteByQuery functionality is needed, it's best if they can   be batched and executed together so they may share the same index reader.   *  * @author yonik  * @version $Id$  * @since solr 0.9  */
end_comment

begin_class
DECL|class|DirectUpdateHandler2
specifier|public
class|class
name|DirectUpdateHandler2
extends|extends
name|UpdateHandler
block|{
comment|// stats
DECL|field|addCommands
name|AtomicLong
name|addCommands
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|addCommandsCumulative
name|AtomicLong
name|addCommandsCumulative
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|deleteByIdCommands
name|AtomicLong
name|deleteByIdCommands
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|deleteByIdCommandsCumulative
name|AtomicLong
name|deleteByIdCommandsCumulative
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|deleteByQueryCommands
name|AtomicLong
name|deleteByQueryCommands
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|deleteByQueryCommandsCumulative
name|AtomicLong
name|deleteByQueryCommandsCumulative
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|commitCommands
name|AtomicLong
name|commitCommands
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|optimizeCommands
name|AtomicLong
name|optimizeCommands
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|numDocsDeleted
name|AtomicLong
name|numDocsDeleted
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|numDocsPending
name|AtomicLong
name|numDocsPending
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|numErrors
name|AtomicLong
name|numErrors
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|numErrorsCumulative
name|AtomicLong
name|numErrorsCumulative
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|// The key is the id, the value (Integer) is the number
comment|// of docs to save (delete all except the last "n" added)
DECL|field|pset
specifier|protected
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|pset
decl_stmt|;
comment|// commonly used constants for the count in the pset
DECL|field|ZERO
specifier|protected
specifier|final
specifier|static
name|Integer
name|ZERO
init|=
literal|0
decl_stmt|;
DECL|field|ONE
specifier|protected
specifier|final
specifier|static
name|Integer
name|ONE
init|=
literal|1
decl_stmt|;
DECL|field|writer
specifier|protected
name|IndexWriter
name|writer
decl_stmt|;
DECL|field|searcher
specifier|protected
name|SolrIndexSearcher
name|searcher
decl_stmt|;
DECL|method|DirectUpdateHandler2
specifier|public
name|DirectUpdateHandler2
parameter_list|(
name|SolrCore
name|core
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|core
argument_list|)
expr_stmt|;
name|pset
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|(
literal|256
argument_list|)
expr_stmt|;
comment|// 256 is just an optional head-start
block|}
DECL|method|openWriter
specifier|protected
name|void
name|openWriter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
name|writer
operator|=
name|createMainIndexWriter
argument_list|(
literal|"DirectUpdateHandler2"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|closeWriter
specifier|protected
name|void
name|closeWriter
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|numDocsPending
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
comment|// if an exception causes the writelock to not be
comment|// released, we could try and delete it here
name|writer
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|openSearcher
specifier|protected
name|void
name|openSearcher
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|searcher
operator|==
literal|null
condition|)
block|{
name|searcher
operator|=
name|core
operator|.
name|newSearcher
argument_list|(
literal|"DirectUpdateHandler2"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|closeSearcher
specifier|protected
name|void
name|closeSearcher
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|searcher
operator|!=
literal|null
condition|)
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
comment|// if an exception causes a lock to not be
comment|// released, we could try to delete it.
name|searcher
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|doAdd
specifier|protected
name|void
name|doAdd
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|closeSearcher
argument_list|()
expr_stmt|;
name|openWriter
argument_list|()
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|addDoc
specifier|public
name|int
name|addDoc
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|addCommands
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|addCommandsCumulative
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|int
name|rc
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|cmd
operator|.
name|allowDups
operator|&&
operator|!
name|cmd
operator|.
name|overwritePending
operator|&&
operator|!
name|cmd
operator|.
name|overwriteCommitted
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"unsupported param combo:"
operator|+
name|cmd
argument_list|)
throw|;
comment|// this would need a reader to implement (to be able to check committed
comment|// before adding.)
comment|// return addNoOverwriteNoDups(cmd);
block|}
elseif|else
if|if
condition|(
operator|!
name|cmd
operator|.
name|allowDups
operator|&&
operator|!
name|cmd
operator|.
name|overwritePending
operator|&&
name|cmd
operator|.
name|overwriteCommitted
condition|)
block|{
name|rc
operator|=
name|addConditionally
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|cmd
operator|.
name|allowDups
operator|&&
name|cmd
operator|.
name|overwritePending
operator|&&
operator|!
name|cmd
operator|.
name|overwriteCommitted
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"unsupported param combo:"
operator|+
name|cmd
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|cmd
operator|.
name|allowDups
operator|&&
name|cmd
operator|.
name|overwritePending
operator|&&
name|cmd
operator|.
name|overwriteCommitted
condition|)
block|{
name|rc
operator|=
name|overwriteBoth
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
elseif|else
if|if
condition|(
name|cmd
operator|.
name|allowDups
operator|&&
operator|!
name|cmd
operator|.
name|overwritePending
operator|&&
operator|!
name|cmd
operator|.
name|overwriteCommitted
condition|)
block|{
name|rc
operator|=
name|allowDups
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
elseif|else
if|if
condition|(
name|cmd
operator|.
name|allowDups
operator|&&
operator|!
name|cmd
operator|.
name|overwritePending
operator|&&
name|cmd
operator|.
name|overwriteCommitted
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"unsupported param combo:"
operator|+
name|cmd
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|cmd
operator|.
name|allowDups
operator|&&
name|cmd
operator|.
name|overwritePending
operator|&&
operator|!
name|cmd
operator|.
name|overwriteCommitted
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"unsupported param combo:"
operator|+
name|cmd
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|cmd
operator|.
name|allowDups
operator|&&
name|cmd
operator|.
name|overwritePending
operator|&&
name|cmd
operator|.
name|overwriteCommitted
condition|)
block|{
name|rc
operator|=
name|overwriteBoth
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"unsupported param combo:"
operator|+
name|cmd
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|rc
operator|!=
literal|1
condition|)
block|{
name|numErrors
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|numErrorsCumulative
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|numDocsPending
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// could return the number of docs deleted, but is that always possible to know???
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|deleteByIdCommands
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|deleteByIdCommandsCumulative
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|cmd
operator|.
name|fromPending
operator|&&
operator|!
name|cmd
operator|.
name|fromCommitted
condition|)
block|{
name|numErrors
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|numErrorsCumulative
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"meaningless command: "
operator|+
name|cmd
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|cmd
operator|.
name|fromPending
operator|||
operator|!
name|cmd
operator|.
name|fromCommitted
condition|)
block|{
name|numErrors
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|numErrorsCumulative
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"operation not supported"
operator|+
name|cmd
argument_list|)
throw|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|pset
operator|.
name|put
argument_list|(
name|cmd
operator|.
name|id
argument_list|,
name|ZERO
argument_list|)
expr_stmt|;
block|}
block|}
comment|// why not return number of docs deleted?
comment|// Depending on implementation, we may not be able to immediately determine the num...
DECL|method|deleteByQuery
specifier|public
name|void
name|deleteByQuery
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
name|deleteByQueryCommands
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|deleteByQueryCommandsCumulative
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|cmd
operator|.
name|fromPending
operator|&&
operator|!
name|cmd
operator|.
name|fromCommitted
condition|)
block|{
name|numErrors
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|numErrorsCumulative
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"meaningless command: "
operator|+
name|cmd
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|cmd
operator|.
name|fromPending
operator|||
operator|!
name|cmd
operator|.
name|fromCommitted
condition|)
block|{
name|numErrors
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|numErrorsCumulative
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SolrException
argument_list|(
literal|400
argument_list|,
literal|"operation not supported"
operator|+
name|cmd
argument_list|)
throw|;
block|}
name|boolean
name|madeIt
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Query
name|q
init|=
name|QueryParsing
operator|.
name|parseQuery
argument_list|(
name|cmd
operator|.
name|query
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|int
name|totDeleted
init|=
literal|0
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// we need to do much of the commit logic (mainly doing queued
comment|// deletes since deleteByQuery can throw off our counts.
name|doDeletions
argument_list|()
expr_stmt|;
name|closeWriter
argument_list|()
expr_stmt|;
name|openSearcher
argument_list|()
expr_stmt|;
comment|// if we want to count the number of docs that were deleted, then
comment|// we need a new instance of the DeleteHitCollector
specifier|final
name|DeleteHitCollector
name|deleter
init|=
operator|new
name|DeleteHitCollector
argument_list|(
name|searcher
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|null
argument_list|,
name|deleter
argument_list|)
expr_stmt|;
name|totDeleted
operator|=
name|deleter
operator|.
name|deleted
expr_stmt|;
block|}
if|if
condition|(
name|SolrCore
operator|.
name|log
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINE
argument_list|)
condition|)
block|{
name|SolrCore
operator|.
name|log
operator|.
name|fine
argument_list|(
literal|"docs deleted by query:"
operator|+
name|totDeleted
argument_list|)
expr_stmt|;
block|}
name|numDocsDeleted
operator|.
name|getAndAdd
argument_list|(
name|totDeleted
argument_list|)
expr_stmt|;
name|madeIt
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|madeIt
condition|)
block|{
name|numErrors
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|numErrorsCumulative
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|///////////////////////////////////////////////////////////////////
comment|/////////////////// helper method for each add type ///////////////
comment|///////////////////////////////////////////////////////////////////
DECL|method|addConditionally
specifier|protected
name|int
name|addConditionally
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cmd
operator|.
name|id
operator|==
literal|null
condition|)
block|{
name|cmd
operator|.
name|id
operator|=
name|getId
argument_list|(
name|cmd
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|Integer
name|saveCount
init|=
name|pset
operator|.
name|get
argument_list|(
name|cmd
operator|.
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|saveCount
operator|!=
literal|null
operator|&&
name|saveCount
operator|!=
literal|0
condition|)
block|{
comment|// a doc with this id already exists in the pending set
return|return
literal|0
return|;
block|}
name|pset
operator|.
name|put
argument_list|(
name|cmd
operator|.
name|id
argument_list|,
name|ONE
argument_list|)
expr_stmt|;
name|doAdd
argument_list|(
name|cmd
operator|.
name|doc
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
comment|// overwrite both pending and committed
DECL|method|overwriteBoth
specifier|protected
specifier|synchronized
name|int
name|overwriteBoth
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cmd
operator|.
name|id
operator|==
literal|null
condition|)
block|{
name|cmd
operator|.
name|id
operator|=
name|getId
argument_list|(
name|cmd
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|pset
operator|.
name|put
argument_list|(
name|cmd
operator|.
name|id
argument_list|,
name|ONE
argument_list|)
expr_stmt|;
name|doAdd
argument_list|(
name|cmd
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
literal|1
return|;
block|}
comment|// add without checking
DECL|method|allowDups
specifier|protected
specifier|synchronized
name|int
name|allowDups
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cmd
operator|.
name|id
operator|==
literal|null
condition|)
block|{
name|cmd
operator|.
name|id
operator|=
name|getOptId
argument_list|(
name|cmd
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
name|doAdd
argument_list|(
name|cmd
operator|.
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmd
operator|.
name|id
operator|!=
literal|null
condition|)
block|{
name|Integer
name|saveCount
init|=
name|pset
operator|.
name|get
argument_list|(
name|cmd
operator|.
name|id
argument_list|)
decl_stmt|;
comment|// if there weren't any docs marked for deletion before, then don't mark
comment|// any for deletion now.
if|if
condition|(
name|saveCount
operator|==
literal|null
condition|)
return|return
literal|1
return|;
comment|// If there were docs marked for deletion, then increment the number of
comment|// docs to save at the end.
comment|// the following line is optional, but it saves an allocation in the common case.
if|if
condition|(
name|saveCount
operator|==
name|ZERO
condition|)
name|saveCount
operator|=
name|ONE
expr_stmt|;
else|else
name|saveCount
operator|++
expr_stmt|;
name|pset
operator|.
name|put
argument_list|(
name|cmd
operator|.
name|id
argument_list|,
name|saveCount
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|1
return|;
block|}
comment|// NOT FOR USE OUTSIDE OF A "synchronized(this)" BLOCK
DECL|field|docnums
specifier|private
name|int
index|[]
name|docnums
decl_stmt|;
comment|//
comment|// do all needed deletions.
comment|// call in a synchronized context.
comment|//
DECL|method|doDeletions
specifier|protected
name|void
name|doDeletions
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pset
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// optimization: only open searcher if there is something to delete...
name|log
operator|.
name|info
argument_list|(
literal|"DirectUpdateHandler2 deleting and removing dups for "
operator|+
name|pset
operator|.
name|size
argument_list|()
operator|+
literal|" ids"
argument_list|)
expr_stmt|;
name|int
name|numDeletes
init|=
literal|0
decl_stmt|;
name|closeWriter
argument_list|()
expr_stmt|;
name|openSearcher
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|searcher
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|TermDocs
name|tdocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
name|String
name|fieldname
init|=
name|idField
operator|.
name|getName
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|pset
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|id
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|int
name|saveLast
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// save the last "saveLast" documents
comment|//expand our array that keeps track of docs if needed.
if|if
condition|(
name|docnums
operator|==
literal|null
operator|||
name|saveLast
operator|>
name|docnums
operator|.
name|length
condition|)
block|{
name|docnums
operator|=
operator|new
name|int
index|[
name|saveLast
index|]
expr_stmt|;
block|}
comment|// initialize all docnums in the list to -1 (unused)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|saveLast
condition|;
name|i
operator|++
control|)
block|{
name|docnums
index|[
name|i
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|tdocs
operator|.
name|seek
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldname
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
comment|//
comment|// record the docs for this term in the "docnums" array and wrap around
comment|// at size "saveLast".  If we reuse a slot in the array, then we delete
comment|// the doc that was there from the index.
comment|//
name|int
name|pos
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|tdocs
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
name|saveLast
operator|==
literal|0
condition|)
block|{
comment|// special case - delete all the docs as we see them.
name|reader
operator|.
name|deleteDocument
argument_list|(
name|tdocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
name|numDeletes
operator|++
expr_stmt|;
continue|continue;
block|}
name|int
name|prev
init|=
name|docnums
index|[
name|pos
index|]
decl_stmt|;
name|docnums
index|[
name|pos
index|]
operator|=
name|tdocs
operator|.
name|doc
argument_list|()
expr_stmt|;
if|if
condition|(
name|prev
operator|!=
operator|-
literal|1
condition|)
block|{
name|reader
operator|.
name|deleteDocument
argument_list|(
name|prev
argument_list|)
expr_stmt|;
name|numDeletes
operator|++
expr_stmt|;
block|}
if|if
condition|(
operator|++
name|pos
operator|>=
name|saveLast
condition|)
name|pos
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|// should we ever shrink it again, or just clear it?
name|pset
operator|.
name|clear
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"DirectUpdateHandler2 docs deleted="
operator|+
name|numDeletes
argument_list|)
expr_stmt|;
name|numDocsDeleted
operator|.
name|addAndGet
argument_list|(
name|numDeletes
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|commit
specifier|public
name|void
name|commit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cmd
operator|.
name|optimize
condition|)
block|{
name|optimizeCommands
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|commitCommands
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|Future
index|[]
name|waitSearcher
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|waitSearcher
condition|)
block|{
name|waitSearcher
operator|=
operator|new
name|Future
index|[
literal|1
index|]
expr_stmt|;
block|}
name|boolean
name|error
init|=
literal|true
decl_stmt|;
try|try
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"start "
operator|+
name|cmd
argument_list|)
expr_stmt|;
name|doDeletions
argument_list|()
expr_stmt|;
if|if
condition|(
name|cmd
operator|.
name|optimize
condition|)
block|{
name|closeSearcher
argument_list|()
expr_stmt|;
name|openWriter
argument_list|()
expr_stmt|;
name|writer
operator|.
name|optimize
argument_list|()
expr_stmt|;
block|}
name|closeSearcher
argument_list|()
expr_stmt|;
name|closeWriter
argument_list|()
expr_stmt|;
name|callPostCommitCallbacks
argument_list|()
expr_stmt|;
if|if
condition|(
name|cmd
operator|.
name|optimize
condition|)
block|{
name|callPostOptimizeCallbacks
argument_list|()
expr_stmt|;
block|}
comment|// open a new searcher in the sync block to avoid opening it
comment|// after a deleteByQuery changed the index, or in between deletes
comment|// and adds of another commit being done.
name|core
operator|.
name|getSearcher
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
name|waitSearcher
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"end_commit_flush"
argument_list|)
expr_stmt|;
block|}
comment|// end synchronized block
name|error
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
name|addCommands
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|deleteByIdCommands
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|deleteByQueryCommands
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|numErrors
operator|.
name|set
argument_list|(
name|error
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// if we are supposed to wait for the searcher to be registered, then we should do it
comment|// outside of the synchronized block so that other update operations can proceed.
if|if
condition|(
name|waitSearcher
operator|!=
literal|null
operator|&&
name|waitSearcher
index|[
literal|0
index|]
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|waitSearcher
index|[
literal|0
index|]
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|log
operator|.
name|info
argument_list|(
literal|"closing "
operator|+
name|this
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|doDeletions
argument_list|()
expr_stmt|;
name|closeSearcher
argument_list|()
expr_stmt|;
name|closeWriter
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"closed "
operator|+
name|this
argument_list|)
expr_stmt|;
block|}
comment|/////////////////////////////////////////////////////////////////////
comment|// SolrInfoMBean stuff: Statistics and Module Info
comment|/////////////////////////////////////////////////////////////////////
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|DirectUpdateHandler2
operator|.
name|class
operator|.
name|getName
argument_list|()
return|;
block|}
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|SolrCore
operator|.
name|version
return|;
block|}
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Update handler that efficiently directly updates the on-disk main lucene index"
return|;
block|}
DECL|method|getCategory
specifier|public
name|Category
name|getCategory
parameter_list|()
block|{
return|return
name|Category
operator|.
name|UPDATEHANDLER
return|;
block|}
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
DECL|method|getDocs
specifier|public
name|URL
index|[]
name|getDocs
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getStatistics
specifier|public
name|NamedList
name|getStatistics
parameter_list|()
block|{
name|NamedList
name|lst
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"commits"
argument_list|,
name|commitCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"optimizes"
argument_list|,
name|optimizeCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"docsPending"
argument_list|,
name|numDocsPending
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// pset.size() not synchronized, but it should be fine to access.
name|lst
operator|.
name|add
argument_list|(
literal|"deletesPending"
argument_list|,
name|pset
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"adds"
argument_list|,
name|addCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"deletesById"
argument_list|,
name|deleteByIdCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"deletesByQuery"
argument_list|,
name|deleteByQueryCommands
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"errors"
argument_list|,
name|numErrors
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_adds"
argument_list|,
name|addCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_deletesById"
argument_list|,
name|deleteByIdCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_deletesByQuery"
argument_list|,
name|deleteByQueryCommandsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"cumulative_errors"
argument_list|,
name|numErrorsCumulative
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|lst
operator|.
name|add
argument_list|(
literal|"docsDeleted"
argument_list|,
name|numDocsDeleted
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|lst
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DirectUpdateHandler2"
operator|+
name|getStatistics
argument_list|()
return|;
block|}
block|}
end_class

end_unit

