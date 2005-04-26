begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|rmi
operator|.
name|Naming
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|RemoteException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|RMISecurityManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|server
operator|.
name|UnicastRemoteObject
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
name|index
operator|.
name|Term
import|;
end_import

begin_comment
comment|/** A remote searchable implementation. */
end_comment

begin_class
DECL|class|RemoteSearchable
specifier|public
class|class
name|RemoteSearchable
extends|extends
name|UnicastRemoteObject
implements|implements
name|Searchable
block|{
DECL|field|local
specifier|private
name|Searchable
name|local
decl_stmt|;
comment|/** Constructs and exports a remote searcher. */
DECL|method|RemoteSearchable
specifier|public
name|RemoteSearchable
parameter_list|(
name|Searchable
name|local
parameter_list|)
throws|throws
name|RemoteException
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|local
operator|=
name|local
expr_stmt|;
block|}
comment|// this implementation should be removed when the deprecated
comment|// Searchable#search(Query,Filter,HitCollector) is removed
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|HitCollector
name|results
parameter_list|)
throws|throws
name|IOException
block|{
name|local
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
DECL|method|search
specifier|public
name|void
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|HitCollector
name|results
parameter_list|)
throws|throws
name|IOException
block|{
name|local
operator|.
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|local
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|docFreq
specifier|public
name|int
name|docFreq
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|local
operator|.
name|docFreq
argument_list|(
name|term
argument_list|)
return|;
block|}
DECL|method|docFreqs
specifier|public
name|int
index|[]
name|docFreqs
parameter_list|(
name|Term
index|[]
name|terms
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|local
operator|.
name|docFreqs
argument_list|(
name|terms
argument_list|)
return|;
block|}
DECL|method|maxDoc
specifier|public
name|int
name|maxDoc
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|local
operator|.
name|maxDoc
argument_list|()
return|;
block|}
comment|// this implementation should be removed when the deprecated
comment|// Searchable#search(Query,Filter,int) is removed
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|local
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|n
argument_list|)
return|;
block|}
DECL|method|search
specifier|public
name|TopDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|local
operator|.
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|n
argument_list|)
return|;
block|}
comment|// this implementation should be removed when the deprecated
comment|// Searchable#search(Query,Filter,int,Sort) is removed
DECL|method|search
specifier|public
name|TopFieldDocs
name|search
parameter_list|(
name|Query
name|query
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|n
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|local
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|filter
argument_list|,
name|n
argument_list|,
name|sort
argument_list|)
return|;
block|}
DECL|method|search
specifier|public
name|TopFieldDocs
name|search
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|int
name|n
parameter_list|,
name|Sort
name|sort
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|local
operator|.
name|search
argument_list|(
name|weight
argument_list|,
name|filter
argument_list|,
name|n
argument_list|,
name|sort
argument_list|)
return|;
block|}
DECL|method|doc
specifier|public
name|Document
name|doc
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|local
operator|.
name|doc
argument_list|(
name|i
argument_list|)
return|;
block|}
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|Query
name|original
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|local
operator|.
name|rewrite
argument_list|(
name|original
argument_list|)
return|;
block|}
comment|// this implementation should be removed when the deprecated
comment|// Searchable#explain(Query,int) is removed
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Query
name|query
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|local
operator|.
name|explain
argument_list|(
name|query
argument_list|,
name|doc
argument_list|)
return|;
block|}
DECL|method|explain
specifier|public
name|Explanation
name|explain
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|local
operator|.
name|explain
argument_list|(
name|weight
argument_list|,
name|doc
argument_list|)
return|;
block|}
comment|/** Exports a searcher for the index in args[0] named    * "//localhost/Searchable". */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
throws|throws
name|Exception
block|{
comment|// create and install a security manager
if|if
condition|(
name|System
operator|.
name|getSecurityManager
argument_list|()
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|setSecurityManager
argument_list|(
operator|new
name|RMISecurityManager
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Searchable
name|local
init|=
operator|new
name|IndexSearcher
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|RemoteSearchable
name|impl
init|=
operator|new
name|RemoteSearchable
argument_list|(
name|local
argument_list|)
decl_stmt|;
comment|// bind the implementation to "Searchable"
name|Naming
operator|.
name|rebind
argument_list|(
literal|"//localhost/Searchable"
argument_list|,
name|impl
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

