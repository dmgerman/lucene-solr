begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*                     Egothor Software License version 1.00                     Copyright (C) 1997-2004 Leo Galambos.                  Copyright (C) 2002-2004 "Egothor developers"                       on behalf of the Egothor Project.                              All rights reserved.     This  software  is  copyrighted  by  the "Egothor developers". If this    license applies to a single file or document, the "Egothor developers"    are the people or entities mentioned as copyright holders in that file    or  document.  If  this  license  applies  to the Egothor project as a    whole,  the  copyright holders are the people or entities mentioned in    the  file CREDITS. This file can be found in the same location as this    license in the distribution.     Redistribution  and  use  in  source and binary forms, with or without    modification, are permitted provided that the following conditions are    met:     1. Redistributions  of  source  code  must retain the above copyright        notice, the list of contributors, this list of conditions, and the        following disclaimer.     2. Redistributions  in binary form must reproduce the above copyright        notice, the list of contributors, this list of conditions, and the        disclaimer  that  follows  these  conditions  in the documentation        and/or other materials provided with the distribution.     3. The name "Egothor" must not be used to endorse or promote products        derived  from  this software without prior written permission. For        written permission, please contact Leo.G@seznam.cz     4. Products  derived  from this software may not be called "Egothor",        nor  may  "Egothor"  appear  in  their name, without prior written        permission from Leo.G@seznam.cz.     In addition, we request that you include in the end-user documentation    provided  with  the  redistribution  and/or  in the software itself an    acknowledgement equivalent to the following:    "This product includes software developed by the Egothor Project.     http://egothor.sf.net/"     THIS  SOFTWARE  IS  PROVIDED  ``AS  IS''  AND ANY EXPRESSED OR IMPLIED    WARRANTIES,  INCLUDING,  BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF    MERCHANTABILITY  AND  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.    IN  NO  EVENT  SHALL THE EGOTHOR PROJECT OR ITS CONTRIBUTORS BE LIABLE    FOR   ANY   DIRECT,   INDIRECT,  INCIDENTAL,  SPECIAL,  EXEMPLARY,  OR    CONSEQUENTIAL  DAMAGES  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF    SUBSTITUTE  GOODS  OR  SERVICES;  LOSS  OF  USE,  DATA, OR PROFITS; OR    BUSINESS  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,    WHETHER  IN  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE    OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN    IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.     This  software  consists  of  voluntary  contributions  made  by  many    individuals  on  behalf  of  the  Egothor  Project  and was originally    created by Leo Galambos (Leo.G@seznam.cz).  */
end_comment

begin_package
DECL|package|org.egothor.stemmer
package|package
name|org
operator|.
name|egothor
operator|.
name|stemmer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|List
import|;
end_import

begin_comment
comment|/**  * The MultiTrie is a Trie of Tries.  *<p>  * It stores words and their associated patch commands. The MultiTrie handles  * patch commands broken into their constituent parts, as a MultiTrie does, but  * the commands are delimited by the skip command.  */
end_comment

begin_class
DECL|class|MultiTrie2
specifier|public
class|class
name|MultiTrie2
extends|extends
name|MultiTrie
block|{
comment|/**    * Constructor for the MultiTrie object.    *     * @param is the input stream    * @exception IOException if an I/O error occurs    */
DECL|method|MultiTrie2
specifier|public
name|MultiTrie2
parameter_list|(
name|DataInput
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor for the MultiTrie2 object    *     * @param forward set to<tt>true</tt> if the elements should be read left to    *          right    */
DECL|method|MultiTrie2
specifier|public
name|MultiTrie2
parameter_list|(
name|boolean
name|forward
parameter_list|)
block|{
name|super
argument_list|(
name|forward
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return the element that is stored in a cell associated with the given key.    *     * @param key the key to the cell holding the desired element    * @return the element    */
annotation|@
name|Override
DECL|method|getFully
specifier|public
name|CharSequence
name|getFully
parameter_list|(
name|CharSequence
name|key
parameter_list|)
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|(
name|tries
operator|.
name|size
argument_list|()
operator|*
literal|2
argument_list|)
decl_stmt|;
try|try
block|{
name|CharSequence
name|lastkey
init|=
name|key
decl_stmt|;
name|CharSequence
name|p
index|[]
init|=
operator|new
name|CharSequence
index|[
name|tries
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|char
name|lastch
init|=
literal|' '
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
name|tries
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|CharSequence
name|r
init|=
name|tries
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getFully
argument_list|(
name|lastkey
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
operator|||
operator|(
name|r
operator|.
name|length
argument_list|()
operator|==
literal|1
operator|&&
name|r
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
name|EOM
operator|)
condition|)
block|{
return|return
name|result
return|;
block|}
if|if
condition|(
name|cannotFollow
argument_list|(
name|lastch
argument_list|,
name|r
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|result
return|;
block|}
else|else
block|{
name|lastch
operator|=
name|r
operator|.
name|charAt
argument_list|(
name|r
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
block|}
comment|// key=key.substring(lengthPP(r));
name|p
index|[
name|i
index|]
operator|=
name|r
expr_stmt|;
if|if
condition|(
name|p
index|[
name|i
index|]
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'-'
condition|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|key
operator|=
name|skip
argument_list|(
name|key
argument_list|,
name|lengthPP
argument_list|(
name|p
index|[
name|i
operator|-
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|key
operator|=
name|skip
argument_list|(
name|key
argument_list|,
name|lengthPP
argument_list|(
name|p
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// key = skip(key, lengthPP(r));
name|result
operator|.
name|append
argument_list|(
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|lastkey
operator|=
name|key
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|x
parameter_list|)
block|{}
return|return
name|result
return|;
block|}
comment|/**    * Return the element that is stored as last on a path belonging to the given    * key.    *     * @param key the key associated with the desired element    * @return the element that is stored as last on a path    */
annotation|@
name|Override
DECL|method|getLastOnPath
specifier|public
name|CharSequence
name|getLastOnPath
parameter_list|(
name|CharSequence
name|key
parameter_list|)
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|(
name|tries
operator|.
name|size
argument_list|()
operator|*
literal|2
argument_list|)
decl_stmt|;
try|try
block|{
name|CharSequence
name|lastkey
init|=
name|key
decl_stmt|;
name|CharSequence
name|p
index|[]
init|=
operator|new
name|CharSequence
index|[
name|tries
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|char
name|lastch
init|=
literal|' '
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
name|tries
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|CharSequence
name|r
init|=
name|tries
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getLastOnPath
argument_list|(
name|lastkey
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
operator|||
operator|(
name|r
operator|.
name|length
argument_list|()
operator|==
literal|1
operator|&&
name|r
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
name|EOM
operator|)
condition|)
block|{
return|return
name|result
return|;
block|}
comment|// System.err.println("LP:"+key+" last:"+lastch+" new:"+r);
if|if
condition|(
name|cannotFollow
argument_list|(
name|lastch
argument_list|,
name|r
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|result
return|;
block|}
else|else
block|{
name|lastch
operator|=
name|r
operator|.
name|charAt
argument_list|(
name|r
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
block|}
comment|// key=key.substring(lengthPP(r));
name|p
index|[
name|i
index|]
operator|=
name|r
expr_stmt|;
if|if
condition|(
name|p
index|[
name|i
index|]
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'-'
condition|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|key
operator|=
name|skip
argument_list|(
name|key
argument_list|,
name|lengthPP
argument_list|(
name|p
index|[
name|i
operator|-
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|key
operator|=
name|skip
argument_list|(
name|key
argument_list|,
name|lengthPP
argument_list|(
name|p
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// key = skip(key, lengthPP(r));
name|result
operator|.
name|append
argument_list|(
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|lastkey
operator|=
name|key
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|x
parameter_list|)
block|{}
return|return
name|result
return|;
block|}
comment|/**    * Write this data structure to the given output stream.    *     * @param os the output stream    * @exception IOException if an I/O error occurs    */
annotation|@
name|Override
DECL|method|store
specifier|public
name|void
name|store
parameter_list|(
name|DataOutput
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|store
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add an element to this structure consisting of the given key and patch    * command.     *<p>    * This method will return without executing if the<tt>cmd</tt>    * parameter's length is 0.    *     * @param key the key    * @param cmd the patch command    */
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|CharSequence
name|key
parameter_list|,
name|CharSequence
name|cmd
parameter_list|)
block|{
if|if
condition|(
name|cmd
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
comment|// System.err.println( cmd );
name|CharSequence
name|p
index|[]
init|=
name|decompose
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
name|int
name|levels
init|=
name|p
operator|.
name|length
decl_stmt|;
comment|// System.err.println("levels "+key+" cmd "+cmd+"|"+levels);
while|while
condition|(
name|levels
operator|>=
name|tries
operator|.
name|size
argument_list|()
condition|)
block|{
name|tries
operator|.
name|add
argument_list|(
operator|new
name|Trie
argument_list|(
name|forward
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|CharSequence
name|lastkey
init|=
name|key
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
name|levels
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|key
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|tries
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|p
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|lastkey
operator|=
name|key
expr_stmt|;
block|}
else|else
block|{
name|tries
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|add
argument_list|(
name|lastkey
argument_list|,
name|p
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// System.err.println("-"+key+" "+p[i]+"|"+key.length());
comment|/*        * key=key.substring(lengthPP(p[i]));        */
if|if
condition|(
name|p
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|p
index|[
name|i
index|]
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'-'
condition|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|key
operator|=
name|skip
argument_list|(
name|key
argument_list|,
name|lengthPP
argument_list|(
name|p
index|[
name|i
operator|-
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|key
operator|=
name|skip
argument_list|(
name|key
argument_list|,
name|lengthPP
argument_list|(
name|p
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// System.err.println("--->"+key);
block|}
if|if
condition|(
name|key
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|tries
operator|.
name|get
argument_list|(
name|levels
argument_list|)
operator|.
name|add
argument_list|(
name|key
argument_list|,
name|EOM_NODE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tries
operator|.
name|get
argument_list|(
name|levels
argument_list|)
operator|.
name|add
argument_list|(
name|lastkey
argument_list|,
name|EOM_NODE
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Break the given patch command into its constituent pieces. The pieces are    * delimited by NOOP commands.    *     * @param cmd the patch command    * @return an array containing the pieces of the command    */
DECL|method|decompose
specifier|public
name|CharSequence
index|[]
name|decompose
parameter_list|(
name|CharSequence
name|cmd
parameter_list|)
block|{
name|int
name|parts
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
literal|0
operator|<=
name|i
operator|&&
name|i
operator|<
name|cmd
operator|.
name|length
argument_list|()
condition|;
control|)
block|{
name|int
name|next
init|=
name|dashEven
argument_list|(
name|cmd
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|next
condition|)
block|{
name|parts
operator|++
expr_stmt|;
name|i
operator|=
name|next
operator|+
literal|2
expr_stmt|;
block|}
else|else
block|{
name|parts
operator|++
expr_stmt|;
name|i
operator|=
name|next
expr_stmt|;
block|}
block|}
name|CharSequence
name|part
index|[]
init|=
operator|new
name|CharSequence
index|[
name|parts
index|]
decl_stmt|;
name|int
name|x
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
literal|0
operator|<=
name|i
operator|&&
name|i
operator|<
name|cmd
operator|.
name|length
argument_list|()
condition|;
control|)
block|{
name|int
name|next
init|=
name|dashEven
argument_list|(
name|cmd
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|next
condition|)
block|{
name|part
index|[
name|x
operator|++
index|]
operator|=
name|cmd
operator|.
name|subSequence
argument_list|(
name|i
argument_list|,
name|i
operator|+
literal|2
argument_list|)
expr_stmt|;
name|i
operator|=
name|next
operator|+
literal|2
expr_stmt|;
block|}
else|else
block|{
name|part
index|[
name|x
operator|++
index|]
operator|=
operator|(
name|next
operator|<
literal|0
operator|)
condition|?
name|cmd
operator|.
name|subSequence
argument_list|(
name|i
argument_list|,
name|cmd
operator|.
name|length
argument_list|()
argument_list|)
else|:
name|cmd
operator|.
name|subSequence
argument_list|(
name|i
argument_list|,
name|next
argument_list|)
expr_stmt|;
name|i
operator|=
name|next
expr_stmt|;
block|}
block|}
return|return
name|part
return|;
block|}
comment|/**    * Remove empty rows from the given Trie and return the newly reduced Trie.    *     * @param by the Trie to reduce    * @return the newly reduced Trie    */
annotation|@
name|Override
DECL|method|reduce
specifier|public
name|Trie
name|reduce
parameter_list|(
name|Reduce
name|by
parameter_list|)
block|{
name|List
argument_list|<
name|Trie
argument_list|>
name|h
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Trie
name|trie
range|:
name|tries
control|)
name|h
operator|.
name|add
argument_list|(
name|trie
operator|.
name|reduce
argument_list|(
name|by
argument_list|)
argument_list|)
expr_stmt|;
name|MultiTrie2
name|m
init|=
operator|new
name|MultiTrie2
argument_list|(
name|forward
argument_list|)
decl_stmt|;
name|m
operator|.
name|tries
operator|=
name|h
expr_stmt|;
return|return
name|m
return|;
block|}
DECL|method|cannotFollow
specifier|private
name|boolean
name|cannotFollow
parameter_list|(
name|char
name|after
parameter_list|,
name|char
name|goes
parameter_list|)
block|{
switch|switch
condition|(
name|after
condition|)
block|{
case|case
literal|'-'
case|:
case|case
literal|'D'
case|:
return|return
name|after
operator|==
name|goes
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|skip
specifier|private
name|CharSequence
name|skip
parameter_list|(
name|CharSequence
name|in
parameter_list|,
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|forward
condition|)
block|{
return|return
name|in
operator|.
name|subSequence
argument_list|(
name|count
argument_list|,
name|in
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|in
operator|.
name|subSequence
argument_list|(
literal|0
argument_list|,
name|in
operator|.
name|length
argument_list|()
operator|-
name|count
argument_list|)
return|;
block|}
block|}
DECL|method|dashEven
specifier|private
name|int
name|dashEven
parameter_list|(
name|CharSequence
name|in
parameter_list|,
name|int
name|from
parameter_list|)
block|{
while|while
condition|(
name|from
operator|<
name|in
operator|.
name|length
argument_list|()
condition|)
block|{
if|if
condition|(
name|in
operator|.
name|charAt
argument_list|(
name|from
argument_list|)
operator|==
literal|'-'
condition|)
block|{
return|return
name|from
return|;
block|}
else|else
block|{
name|from
operator|+=
literal|2
expr_stmt|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"fallthrough"
argument_list|)
DECL|method|lengthPP
specifier|private
name|int
name|lengthPP
parameter_list|(
name|CharSequence
name|cmd
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
literal|0
init|;
name|i
operator|<
name|cmd
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|cmd
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
condition|)
block|{
case|case
literal|'-'
case|:
case|case
literal|'D'
case|:
name|len
operator|+=
name|cmd
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|-
literal|'a'
operator|+
literal|1
expr_stmt|;
break|break;
case|case
literal|'R'
case|:
name|len
operator|++
expr_stmt|;
comment|/* intentional fallthrough */
case|case
literal|'I'
case|:
break|break;
block|}
block|}
return|return
name|len
return|;
block|}
block|}
end_class

end_unit

