begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.compound.hyphenation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|compound
operator|.
name|hyphenation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_comment
comment|/**  *<h2>Ternary Search Tree.</h2>  *   *<p>  * A ternary search tree is a hybrid between a binary tree and a digital search  * tree (trie). Keys are limited to strings. A data value of type char is stored  * in each leaf node. It can be used as an index (or pointer) to the data.  * Branches that only contain one key are compressed to one node by storing a  * pointer to the trailer substring of the key. This class is intended to serve  * as base class or helper class to implement Dictionary collections or the  * like. Ternary trees have some nice properties as the following: the tree can  * be traversed in sorted order, partial matches (wildcard) can be implemented,  * retrieval of all keys within a given distance from the target, etc. The  * storage requirements are higher than a binary tree but a lot less than a  * trie. Performance is comparable with a hash table, sometimes it outperforms a  * hash function (most of the time can determine a miss faster than a hash).  *</p>  *   *<p>  * The main purpose of this java port is to serve as a base for implementing  * TeX's hyphenation algorithm (see The TeXBook, appendix H). Each language  * requires from 5000 to 15000 hyphenation patterns which will be keys in this  * tree. The strings patterns are usually small (from 2 to 5 characters), but  * each char in the tree is stored in a node. Thus memory usage is the main  * concern. We will sacrifice 'elegance' to keep memory requirements to the  * minimum. Using java's char type as pointer (yes, I know pointer it is a  * forbidden word in java) we can keep the size of the node to be just 8 bytes  * (3 pointers and the data char). This gives room for about 65000 nodes. In my  * tests the english patterns took 7694 nodes and the german patterns 10055  * nodes, so I think we are safe.  *</p>  *   *<p>  * All said, this is a map with strings as keys and char as value. Pretty  * limited!. It can be extended to a general map by using the string  * representation of an object and using the char value as an index to an array  * that contains the object values.  *</p>  *   * This class has been taken from the Apache FOP project (http://xmlgraphics.apache.org/fop/). They have been slightly modified.   */
end_comment

begin_class
DECL|class|TernaryTree
specifier|public
class|class
name|TernaryTree
implements|implements
name|Cloneable
block|{
comment|/**    * We use 4 arrays to represent a node. I guess I should have created a proper    * node class, but somehow Knuth's pascal code made me forget we now have a    * portable language with virtual memory management and automatic garbage    * collection! And now is kind of late, furthermore, if it ain't broken, don't    * fix it.    */
comment|/**    * Pointer to low branch and to rest of the key when it is stored directly in    * this node, we don't have unions in java!    */
DECL|field|lo
specifier|protected
name|char
index|[]
name|lo
decl_stmt|;
comment|/**    * Pointer to high branch.    */
DECL|field|hi
specifier|protected
name|char
index|[]
name|hi
decl_stmt|;
comment|/**    * Pointer to equal branch and to data when this node is a string terminator.    */
DECL|field|eq
specifier|protected
name|char
index|[]
name|eq
decl_stmt|;
comment|/**    *<P>    * The character stored in this node: splitchar. Two special values are    * reserved:    *</P>    *<ul>    *<li>0x0000 as string terminator</li>    *<li>0xFFFF to indicate that the branch starting at this node is compressed</li>    *</ul>    *<p>    * This shouldn't be a problem if we give the usual semantics to strings since    * 0xFFFF is guaranteed not to be an Unicode character.    *</p>    */
DECL|field|sc
specifier|protected
name|char
index|[]
name|sc
decl_stmt|;
comment|/**    * This vector holds the trailing of the keys when the branch is compressed.    */
DECL|field|kv
specifier|protected
name|CharVector
name|kv
decl_stmt|;
DECL|field|root
specifier|protected
name|char
name|root
decl_stmt|;
DECL|field|freenode
specifier|protected
name|char
name|freenode
decl_stmt|;
DECL|field|length
specifier|protected
name|int
name|length
decl_stmt|;
comment|// number of items in tree
DECL|field|BLOCK_SIZE
specifier|protected
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|2048
decl_stmt|;
comment|// allocation size for arrays
DECL|method|TernaryTree
name|TernaryTree
parameter_list|()
block|{
name|init
argument_list|()
expr_stmt|;
block|}
DECL|method|init
specifier|protected
name|void
name|init
parameter_list|()
block|{
name|root
operator|=
literal|0
expr_stmt|;
name|freenode
operator|=
literal|1
expr_stmt|;
name|length
operator|=
literal|0
expr_stmt|;
name|lo
operator|=
operator|new
name|char
index|[
name|BLOCK_SIZE
index|]
expr_stmt|;
name|hi
operator|=
operator|new
name|char
index|[
name|BLOCK_SIZE
index|]
expr_stmt|;
name|eq
operator|=
operator|new
name|char
index|[
name|BLOCK_SIZE
index|]
expr_stmt|;
name|sc
operator|=
operator|new
name|char
index|[
name|BLOCK_SIZE
index|]
expr_stmt|;
name|kv
operator|=
operator|new
name|CharVector
argument_list|()
expr_stmt|;
block|}
comment|/**    * Branches are initially compressed, needing one node per key plus the size    * of the string key. They are decompressed as needed when another key with    * same prefix is inserted. This saves a lot of space, specially for long    * keys.    */
DECL|method|insert
specifier|public
name|void
name|insert
parameter_list|(
name|String
name|key
parameter_list|,
name|char
name|val
parameter_list|)
block|{
comment|// make sure we have enough room in the arrays
name|int
name|len
init|=
name|key
operator|.
name|length
argument_list|()
operator|+
literal|1
decl_stmt|;
comment|// maximum number of nodes that may be generated
if|if
condition|(
name|freenode
operator|+
name|len
operator|>
name|eq
operator|.
name|length
condition|)
block|{
name|redimNodeArrays
argument_list|(
name|eq
operator|.
name|length
operator|+
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
block|}
name|char
name|strkey
index|[]
init|=
operator|new
name|char
index|[
name|len
operator|--
index|]
decl_stmt|;
name|key
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|len
argument_list|,
name|strkey
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|strkey
index|[
name|len
index|]
operator|=
literal|0
expr_stmt|;
name|root
operator|=
name|insert
argument_list|(
name|root
argument_list|,
name|strkey
argument_list|,
literal|0
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
DECL|method|insert
specifier|public
name|void
name|insert
parameter_list|(
name|char
index|[]
name|key
parameter_list|,
name|int
name|start
parameter_list|,
name|char
name|val
parameter_list|)
block|{
name|int
name|len
init|=
name|strlen
argument_list|(
name|key
argument_list|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|freenode
operator|+
name|len
operator|>
name|eq
operator|.
name|length
condition|)
block|{
name|redimNodeArrays
argument_list|(
name|eq
operator|.
name|length
operator|+
name|BLOCK_SIZE
argument_list|)
expr_stmt|;
block|}
name|root
operator|=
name|insert
argument_list|(
name|root
argument_list|,
name|key
argument_list|,
name|start
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
comment|/**    * The actual insertion function, recursive version.    */
DECL|method|insert
specifier|private
name|char
name|insert
parameter_list|(
name|char
name|p
parameter_list|,
name|char
index|[]
name|key
parameter_list|,
name|int
name|start
parameter_list|,
name|char
name|val
parameter_list|)
block|{
name|int
name|len
init|=
name|strlen
argument_list|(
name|key
argument_list|,
name|start
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|0
condition|)
block|{
comment|// this means there is no branch, this node will start a new branch.
comment|// Instead of doing that, we store the key somewhere else and create
comment|// only one node with a pointer to the key
name|p
operator|=
name|freenode
operator|++
expr_stmt|;
name|eq
index|[
name|p
index|]
operator|=
name|val
expr_stmt|;
comment|// holds data
name|length
operator|++
expr_stmt|;
name|hi
index|[
name|p
index|]
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|sc
index|[
name|p
index|]
operator|=
literal|0xFFFF
expr_stmt|;
comment|// indicates branch is compressed
name|lo
index|[
name|p
index|]
operator|=
operator|(
name|char
operator|)
name|kv
operator|.
name|alloc
argument_list|(
name|len
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// use 'lo' to hold pointer to key
name|strcpy
argument_list|(
name|kv
operator|.
name|getArray
argument_list|()
argument_list|,
name|lo
index|[
name|p
index|]
argument_list|,
name|key
argument_list|,
name|start
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sc
index|[
name|p
index|]
operator|=
literal|0
expr_stmt|;
name|lo
index|[
name|p
index|]
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|p
return|;
block|}
if|if
condition|(
name|sc
index|[
name|p
index|]
operator|==
literal|0xFFFF
condition|)
block|{
comment|// branch is compressed: need to decompress
comment|// this will generate garbage in the external key array
comment|// but we can do some garbage collection later
name|char
name|pp
init|=
name|freenode
operator|++
decl_stmt|;
name|lo
index|[
name|pp
index|]
operator|=
name|lo
index|[
name|p
index|]
expr_stmt|;
comment|// previous pointer to key
name|eq
index|[
name|pp
index|]
operator|=
name|eq
index|[
name|p
index|]
expr_stmt|;
comment|// previous pointer to data
name|lo
index|[
name|p
index|]
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|sc
index|[
name|p
index|]
operator|=
name|kv
operator|.
name|get
argument_list|(
name|lo
index|[
name|pp
index|]
argument_list|)
expr_stmt|;
name|eq
index|[
name|p
index|]
operator|=
name|pp
expr_stmt|;
name|lo
index|[
name|pp
index|]
operator|++
expr_stmt|;
if|if
condition|(
name|kv
operator|.
name|get
argument_list|(
name|lo
index|[
name|pp
index|]
argument_list|)
operator|==
literal|0
condition|)
block|{
comment|// key completly decompressed leaving garbage in key array
name|lo
index|[
name|pp
index|]
operator|=
literal|0
expr_stmt|;
name|sc
index|[
name|pp
index|]
operator|=
literal|0
expr_stmt|;
name|hi
index|[
name|pp
index|]
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
comment|// we only got first char of key, rest is still there
name|sc
index|[
name|pp
index|]
operator|=
literal|0xFFFF
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// In this case we can save a node by swapping the new node
comment|// with the compressed node
name|sc
index|[
name|pp
index|]
operator|=
literal|0xFFFF
expr_stmt|;
name|hi
index|[
name|p
index|]
operator|=
name|pp
expr_stmt|;
name|sc
index|[
name|p
index|]
operator|=
literal|0
expr_stmt|;
name|eq
index|[
name|p
index|]
operator|=
name|val
expr_stmt|;
name|length
operator|++
expr_stmt|;
return|return
name|p
return|;
block|}
block|}
name|char
name|s
init|=
name|key
index|[
name|start
index|]
decl_stmt|;
if|if
condition|(
name|s
operator|<
name|sc
index|[
name|p
index|]
condition|)
block|{
name|lo
index|[
name|p
index|]
operator|=
name|insert
argument_list|(
name|lo
index|[
name|p
index|]
argument_list|,
name|key
argument_list|,
name|start
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s
operator|==
name|sc
index|[
name|p
index|]
condition|)
block|{
if|if
condition|(
name|s
operator|!=
literal|0
condition|)
block|{
name|eq
index|[
name|p
index|]
operator|=
name|insert
argument_list|(
name|eq
index|[
name|p
index|]
argument_list|,
name|key
argument_list|,
name|start
operator|+
literal|1
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// key already in tree, overwrite data
name|eq
index|[
name|p
index|]
operator|=
name|val
expr_stmt|;
block|}
block|}
else|else
block|{
name|hi
index|[
name|p
index|]
operator|=
name|insert
argument_list|(
name|hi
index|[
name|p
index|]
argument_list|,
name|key
argument_list|,
name|start
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
return|return
name|p
return|;
block|}
comment|/**    * Compares 2 null terminated char arrays    */
DECL|method|strcmp
specifier|public
specifier|static
name|int
name|strcmp
parameter_list|(
name|char
index|[]
name|a
parameter_list|,
name|int
name|startA
parameter_list|,
name|char
index|[]
name|b
parameter_list|,
name|int
name|startB
parameter_list|)
block|{
for|for
control|(
init|;
name|a
index|[
name|startA
index|]
operator|==
name|b
index|[
name|startB
index|]
condition|;
name|startA
operator|++
operator|,
name|startB
operator|++
control|)
block|{
if|if
condition|(
name|a
index|[
name|startA
index|]
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
block|}
return|return
name|a
index|[
name|startA
index|]
operator|-
name|b
index|[
name|startB
index|]
return|;
block|}
comment|/**    * Compares a string with null terminated char array    */
DECL|method|strcmp
specifier|public
specifier|static
name|int
name|strcmp
parameter_list|(
name|String
name|str
parameter_list|,
name|char
index|[]
name|a
parameter_list|,
name|int
name|start
parameter_list|)
block|{
name|int
name|i
decl_stmt|,
name|d
decl_stmt|,
name|len
init|=
name|str
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|d
operator|=
operator|(
name|int
operator|)
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|-
name|a
index|[
name|start
operator|+
name|i
index|]
expr_stmt|;
if|if
condition|(
name|d
operator|!=
literal|0
condition|)
block|{
return|return
name|d
return|;
block|}
if|if
condition|(
name|a
index|[
name|start
operator|+
name|i
index|]
operator|==
literal|0
condition|)
block|{
return|return
name|d
return|;
block|}
block|}
if|if
condition|(
name|a
index|[
name|start
operator|+
name|i
index|]
operator|!=
literal|0
condition|)
block|{
return|return
operator|-
name|a
index|[
name|start
operator|+
name|i
index|]
return|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|strcpy
specifier|public
specifier|static
name|void
name|strcpy
parameter_list|(
name|char
index|[]
name|dst
parameter_list|,
name|int
name|di
parameter_list|,
name|char
index|[]
name|src
parameter_list|,
name|int
name|si
parameter_list|)
block|{
while|while
condition|(
name|src
index|[
name|si
index|]
operator|!=
literal|0
condition|)
block|{
name|dst
index|[
name|di
operator|++
index|]
operator|=
name|src
index|[
name|si
operator|++
index|]
expr_stmt|;
block|}
name|dst
index|[
name|di
index|]
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|strlen
specifier|public
specifier|static
name|int
name|strlen
parameter_list|(
name|char
index|[]
name|a
parameter_list|,
name|int
name|start
parameter_list|)
block|{
name|int
name|len
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|a
operator|.
name|length
operator|&&
name|a
index|[
name|i
index|]
operator|!=
literal|0
condition|;
name|i
operator|++
control|)
block|{
name|len
operator|++
expr_stmt|;
block|}
return|return
name|len
return|;
block|}
DECL|method|strlen
specifier|public
specifier|static
name|int
name|strlen
parameter_list|(
name|char
index|[]
name|a
parameter_list|)
block|{
return|return
name|strlen
argument_list|(
name|a
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|find
specifier|public
name|int
name|find
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|int
name|len
init|=
name|key
operator|.
name|length
argument_list|()
decl_stmt|;
name|char
name|strkey
index|[]
init|=
operator|new
name|char
index|[
name|len
operator|+
literal|1
index|]
decl_stmt|;
name|key
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|len
argument_list|,
name|strkey
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|strkey
index|[
name|len
index|]
operator|=
literal|0
expr_stmt|;
return|return
name|find
argument_list|(
name|strkey
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|find
specifier|public
name|int
name|find
parameter_list|(
name|char
index|[]
name|key
parameter_list|,
name|int
name|start
parameter_list|)
block|{
name|int
name|d
decl_stmt|;
name|char
name|p
init|=
name|root
decl_stmt|;
name|int
name|i
init|=
name|start
decl_stmt|;
name|char
name|c
decl_stmt|;
while|while
condition|(
name|p
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|sc
index|[
name|p
index|]
operator|==
literal|0xFFFF
condition|)
block|{
if|if
condition|(
name|strcmp
argument_list|(
name|key
argument_list|,
name|i
argument_list|,
name|kv
operator|.
name|getArray
argument_list|()
argument_list|,
name|lo
index|[
name|p
index|]
argument_list|)
operator|==
literal|0
condition|)
block|{
return|return
name|eq
index|[
name|p
index|]
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
name|c
operator|=
name|key
index|[
name|i
index|]
expr_stmt|;
name|d
operator|=
name|c
operator|-
name|sc
index|[
name|p
index|]
expr_stmt|;
if|if
condition|(
name|d
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|c
operator|==
literal|0
condition|)
block|{
return|return
name|eq
index|[
name|p
index|]
return|;
block|}
name|i
operator|++
expr_stmt|;
name|p
operator|=
name|eq
index|[
name|p
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|d
operator|<
literal|0
condition|)
block|{
name|p
operator|=
name|lo
index|[
name|p
index|]
expr_stmt|;
block|}
else|else
block|{
name|p
operator|=
name|hi
index|[
name|p
index|]
expr_stmt|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|knows
specifier|public
name|boolean
name|knows
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
operator|(
name|find
argument_list|(
name|key
argument_list|)
operator|>=
literal|0
operator|)
return|;
block|}
comment|// redimension the arrays
DECL|method|redimNodeArrays
specifier|private
name|void
name|redimNodeArrays
parameter_list|(
name|int
name|newsize
parameter_list|)
block|{
name|int
name|len
init|=
name|newsize
operator|<
name|lo
operator|.
name|length
condition|?
name|newsize
else|:
name|lo
operator|.
name|length
decl_stmt|;
name|char
index|[]
name|na
init|=
operator|new
name|char
index|[
name|newsize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|lo
argument_list|,
literal|0
argument_list|,
name|na
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|lo
operator|=
name|na
expr_stmt|;
name|na
operator|=
operator|new
name|char
index|[
name|newsize
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|hi
argument_list|,
literal|0
argument_list|,
name|na
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|hi
operator|=
name|na
expr_stmt|;
name|na
operator|=
operator|new
name|char
index|[
name|newsize
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|eq
argument_list|,
literal|0
argument_list|,
name|na
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|eq
operator|=
name|na
expr_stmt|;
name|na
operator|=
operator|new
name|char
index|[
name|newsize
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|sc
argument_list|,
literal|0
argument_list|,
name|na
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|sc
operator|=
name|na
expr_stmt|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|TernaryTree
name|clone
parameter_list|()
block|{
name|TernaryTree
name|t
init|=
operator|new
name|TernaryTree
argument_list|()
decl_stmt|;
name|t
operator|.
name|lo
operator|=
name|this
operator|.
name|lo
operator|.
name|clone
argument_list|()
expr_stmt|;
name|t
operator|.
name|hi
operator|=
name|this
operator|.
name|hi
operator|.
name|clone
argument_list|()
expr_stmt|;
name|t
operator|.
name|eq
operator|=
name|this
operator|.
name|eq
operator|.
name|clone
argument_list|()
expr_stmt|;
name|t
operator|.
name|sc
operator|=
name|this
operator|.
name|sc
operator|.
name|clone
argument_list|()
expr_stmt|;
name|t
operator|.
name|kv
operator|=
name|this
operator|.
name|kv
operator|.
name|clone
argument_list|()
expr_stmt|;
name|t
operator|.
name|root
operator|=
name|this
operator|.
name|root
expr_stmt|;
name|t
operator|.
name|freenode
operator|=
name|this
operator|.
name|freenode
expr_stmt|;
name|t
operator|.
name|length
operator|=
name|this
operator|.
name|length
expr_stmt|;
return|return
name|t
return|;
block|}
comment|/**    * Recursively insert the median first and then the median of the lower and    * upper halves, and so on in order to get a balanced tree. The array of keys    * is assumed to be sorted in ascending order.    */
DECL|method|insertBalanced
specifier|protected
name|void
name|insertBalanced
parameter_list|(
name|String
index|[]
name|k
parameter_list|,
name|char
index|[]
name|v
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|n
parameter_list|)
block|{
name|int
name|m
decl_stmt|;
if|if
condition|(
name|n
operator|<
literal|1
condition|)
block|{
return|return;
block|}
name|m
operator|=
name|n
operator|>>
literal|1
expr_stmt|;
name|insert
argument_list|(
name|k
index|[
name|m
operator|+
name|offset
index|]
argument_list|,
name|v
index|[
name|m
operator|+
name|offset
index|]
argument_list|)
expr_stmt|;
name|insertBalanced
argument_list|(
name|k
argument_list|,
name|v
argument_list|,
name|offset
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|insertBalanced
argument_list|(
name|k
argument_list|,
name|v
argument_list|,
name|offset
operator|+
name|m
operator|+
literal|1
argument_list|,
name|n
operator|-
name|m
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Balance the tree for best search performance    */
DECL|method|balance
specifier|public
name|void
name|balance
parameter_list|()
block|{
comment|// System.out.print("Before root splitchar = ");
comment|// System.out.println(sc[root]);
name|int
name|i
init|=
literal|0
decl_stmt|,
name|n
init|=
name|length
decl_stmt|;
name|String
index|[]
name|k
init|=
operator|new
name|String
index|[
name|n
index|]
decl_stmt|;
name|char
index|[]
name|v
init|=
operator|new
name|char
index|[
name|n
index|]
decl_stmt|;
name|Iterator
name|iter
init|=
operator|new
name|Iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|v
index|[
name|i
index|]
operator|=
name|iter
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|k
index|[
name|i
operator|++
index|]
operator|=
name|iter
operator|.
name|nextElement
argument_list|()
expr_stmt|;
block|}
name|init
argument_list|()
expr_stmt|;
name|insertBalanced
argument_list|(
name|k
argument_list|,
name|v
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
comment|// With uniform letter distribution sc[root] should be around 'm'
comment|// System.out.print("After root splitchar = ");
comment|// System.out.println(sc[root]);
block|}
comment|/**    * Each node stores a character (splitchar) which is part of some key(s). In a    * compressed branch (one that only contain a single string key) the trailer    * of the key which is not already in nodes is stored externally in the kv    * array. As items are inserted, key substrings decrease. Some substrings may    * completely disappear when the whole branch is totally decompressed. The    * tree is traversed to find the key substrings actually used. In addition,    * duplicate substrings are removed using a map (implemented with a    * TernaryTree!).    *     */
DECL|method|trimToSize
specifier|public
name|void
name|trimToSize
parameter_list|()
block|{
comment|// first balance the tree for best performance
name|balance
argument_list|()
expr_stmt|;
comment|// redimension the node arrays
name|redimNodeArrays
argument_list|(
name|freenode
argument_list|)
expr_stmt|;
comment|// ok, compact kv array
name|CharVector
name|kx
init|=
operator|new
name|CharVector
argument_list|()
decl_stmt|;
name|kx
operator|.
name|alloc
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|TernaryTree
name|map
init|=
operator|new
name|TernaryTree
argument_list|()
decl_stmt|;
name|compact
argument_list|(
name|kx
argument_list|,
name|map
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|kv
operator|=
name|kx
expr_stmt|;
name|kv
operator|.
name|trimToSize
argument_list|()
expr_stmt|;
block|}
DECL|method|compact
specifier|private
name|void
name|compact
parameter_list|(
name|CharVector
name|kx
parameter_list|,
name|TernaryTree
name|map
parameter_list|,
name|char
name|p
parameter_list|)
block|{
name|int
name|k
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|sc
index|[
name|p
index|]
operator|==
literal|0xFFFF
condition|)
block|{
name|k
operator|=
name|map
operator|.
name|find
argument_list|(
name|kv
operator|.
name|getArray
argument_list|()
argument_list|,
name|lo
index|[
name|p
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|<
literal|0
condition|)
block|{
name|k
operator|=
name|kx
operator|.
name|alloc
argument_list|(
name|strlen
argument_list|(
name|kv
operator|.
name|getArray
argument_list|()
argument_list|,
name|lo
index|[
name|p
index|]
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
name|strcpy
argument_list|(
name|kx
operator|.
name|getArray
argument_list|()
argument_list|,
name|k
argument_list|,
name|kv
operator|.
name|getArray
argument_list|()
argument_list|,
name|lo
index|[
name|p
index|]
argument_list|)
expr_stmt|;
name|map
operator|.
name|insert
argument_list|(
name|kx
operator|.
name|getArray
argument_list|()
argument_list|,
name|k
argument_list|,
operator|(
name|char
operator|)
name|k
argument_list|)
expr_stmt|;
block|}
name|lo
index|[
name|p
index|]
operator|=
operator|(
name|char
operator|)
name|k
expr_stmt|;
block|}
else|else
block|{
name|compact
argument_list|(
name|kx
argument_list|,
name|map
argument_list|,
name|lo
index|[
name|p
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|sc
index|[
name|p
index|]
operator|!=
literal|0
condition|)
block|{
name|compact
argument_list|(
name|kx
argument_list|,
name|map
argument_list|,
name|eq
index|[
name|p
index|]
argument_list|)
expr_stmt|;
block|}
name|compact
argument_list|(
name|kx
argument_list|,
name|map
argument_list|,
name|hi
index|[
name|p
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|keys
specifier|public
name|Enumeration
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|()
return|;
block|}
DECL|class|Iterator
specifier|public
class|class
name|Iterator
implements|implements
name|Enumeration
argument_list|<
name|String
argument_list|>
block|{
comment|/**      * current node index      */
DECL|field|cur
name|int
name|cur
decl_stmt|;
comment|/**      * current key      */
DECL|field|curkey
name|String
name|curkey
decl_stmt|;
DECL|class|Item
specifier|private
class|class
name|Item
implements|implements
name|Cloneable
block|{
DECL|field|parent
name|char
name|parent
decl_stmt|;
DECL|field|child
name|char
name|child
decl_stmt|;
DECL|method|Item
specifier|public
name|Item
parameter_list|()
block|{
name|parent
operator|=
literal|0
expr_stmt|;
name|child
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|Item
specifier|public
name|Item
parameter_list|(
name|char
name|p
parameter_list|,
name|char
name|c
parameter_list|)
block|{
name|parent
operator|=
name|p
expr_stmt|;
name|child
operator|=
name|c
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Item
name|clone
parameter_list|()
block|{
return|return
operator|new
name|Item
argument_list|(
name|parent
argument_list|,
name|child
argument_list|)
return|;
block|}
block|}
comment|/**      * Node stack      */
DECL|field|ns
name|Stack
argument_list|<
name|Item
argument_list|>
name|ns
decl_stmt|;
comment|/**      * key stack implemented with a StringBuilder      */
DECL|field|ks
name|StringBuilder
name|ks
decl_stmt|;
DECL|method|Iterator
specifier|public
name|Iterator
parameter_list|()
block|{
name|cur
operator|=
operator|-
literal|1
expr_stmt|;
name|ns
operator|=
operator|new
name|Stack
argument_list|<>
argument_list|()
expr_stmt|;
name|ks
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|rewind
argument_list|()
expr_stmt|;
block|}
DECL|method|rewind
specifier|public
name|void
name|rewind
parameter_list|()
block|{
name|ns
operator|.
name|removeAllElements
argument_list|()
expr_stmt|;
name|ks
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|cur
operator|=
name|root
expr_stmt|;
name|run
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nextElement
specifier|public
name|String
name|nextElement
parameter_list|()
block|{
name|String
name|res
init|=
name|curkey
decl_stmt|;
name|cur
operator|=
name|up
argument_list|()
expr_stmt|;
name|run
argument_list|()
expr_stmt|;
return|return
name|res
return|;
block|}
DECL|method|getValue
specifier|public
name|char
name|getValue
parameter_list|()
block|{
if|if
condition|(
name|cur
operator|>=
literal|0
condition|)
block|{
return|return
name|eq
index|[
name|cur
index|]
return|;
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|hasMoreElements
specifier|public
name|boolean
name|hasMoreElements
parameter_list|()
block|{
return|return
operator|(
name|cur
operator|!=
operator|-
literal|1
operator|)
return|;
block|}
comment|/**      * traverse upwards      */
DECL|method|up
specifier|private
name|int
name|up
parameter_list|()
block|{
name|Item
name|i
init|=
operator|new
name|Item
argument_list|()
decl_stmt|;
name|int
name|res
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|ns
operator|.
name|empty
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|cur
operator|!=
literal|0
operator|&&
name|sc
index|[
name|cur
index|]
operator|==
literal|0
condition|)
block|{
return|return
name|lo
index|[
name|cur
index|]
return|;
block|}
name|boolean
name|climb
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|climb
condition|)
block|{
name|i
operator|=
name|ns
operator|.
name|pop
argument_list|()
expr_stmt|;
name|i
operator|.
name|child
operator|++
expr_stmt|;
switch|switch
condition|(
name|i
operator|.
name|child
condition|)
block|{
case|case
literal|1
case|:
if|if
condition|(
name|sc
index|[
name|i
operator|.
name|parent
index|]
operator|!=
literal|0
condition|)
block|{
name|res
operator|=
name|eq
index|[
name|i
operator|.
name|parent
index|]
expr_stmt|;
name|ns
operator|.
name|push
argument_list|(
name|i
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
name|ks
operator|.
name|append
argument_list|(
name|sc
index|[
name|i
operator|.
name|parent
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|i
operator|.
name|child
operator|++
expr_stmt|;
name|ns
operator|.
name|push
argument_list|(
name|i
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
name|res
operator|=
name|hi
index|[
name|i
operator|.
name|parent
index|]
expr_stmt|;
block|}
name|climb
operator|=
literal|false
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|res
operator|=
name|hi
index|[
name|i
operator|.
name|parent
index|]
expr_stmt|;
name|ns
operator|.
name|push
argument_list|(
name|i
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ks
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ks
operator|.
name|setLength
argument_list|(
name|ks
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// pop
block|}
name|climb
operator|=
literal|false
expr_stmt|;
break|break;
default|default:
if|if
condition|(
name|ns
operator|.
name|empty
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|climb
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
return|return
name|res
return|;
block|}
comment|/**      * traverse the tree to find next key      */
DECL|method|run
specifier|private
name|int
name|run
parameter_list|()
block|{
if|if
condition|(
name|cur
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|boolean
name|leaf
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// first go down on low branch until leaf or compressed branch
while|while
condition|(
name|cur
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|sc
index|[
name|cur
index|]
operator|==
literal|0xFFFF
condition|)
block|{
name|leaf
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|ns
operator|.
name|push
argument_list|(
operator|new
name|Item
argument_list|(
operator|(
name|char
operator|)
name|cur
argument_list|,
literal|'\u0000'
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|sc
index|[
name|cur
index|]
operator|==
literal|0
condition|)
block|{
name|leaf
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|cur
operator|=
name|lo
index|[
name|cur
index|]
expr_stmt|;
block|}
if|if
condition|(
name|leaf
condition|)
block|{
break|break;
block|}
comment|// nothing found, go up one node and try again
name|cur
operator|=
name|up
argument_list|()
expr_stmt|;
if|if
condition|(
name|cur
operator|==
operator|-
literal|1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|// The current node should be a data node and
comment|// the key should be in the key stack (at least partially)
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
name|ks
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sc
index|[
name|cur
index|]
operator|==
literal|0xFFFF
condition|)
block|{
name|int
name|p
init|=
name|lo
index|[
name|cur
index|]
decl_stmt|;
while|while
condition|(
name|kv
operator|.
name|get
argument_list|(
name|p
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|kv
operator|.
name|get
argument_list|(
name|p
operator|++
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|curkey
operator|=
name|buf
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
DECL|method|printStats
specifier|public
name|void
name|printStats
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"Number of keys = "
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Node count = "
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|freenode
argument_list|)
argument_list|)
expr_stmt|;
comment|// System.out.println("Array length = " + Integer.toString(eq.length));
name|out
operator|.
name|println
argument_list|(
literal|"Key Array length = "
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|kv
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|/*      * for(int i=0; i<kv.length(); i++) if ( kv.get(i) != 0 )      * System.out.print(kv.get(i)); else System.out.println("");      * System.out.println("Keys:"); for(Enumeration enum = keys();      * enum.hasMoreElements(); ) System.out.println(enum.nextElement());      */
block|}
comment|/*   public static void main(String[] args) {     TernaryTree tt = new TernaryTree();     tt.insert("Carlos", 'C');     tt.insert("Car", 'r');     tt.insert("palos", 'l');     tt.insert("pa", 'p');     tt.trimToSize();     System.out.println((char) tt.find("Car"));     System.out.println((char) tt.find("Carlos"));     System.out.println((char) tt.find("alto"));     tt.printStats(System.out);   }   */
block|}
end_class

end_unit

