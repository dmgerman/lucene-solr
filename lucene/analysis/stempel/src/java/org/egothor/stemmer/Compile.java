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
name|BufferedOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|LineNumberReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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
name|SuppressForbidden
import|;
end_import

begin_comment
comment|/**  * The Compile class is used to compile a stemmer table.  */
end_comment

begin_class
DECL|class|Compile
specifier|public
class|class
name|Compile
block|{
DECL|field|backward
specifier|static
name|boolean
name|backward
decl_stmt|;
DECL|field|multi
specifier|static
name|boolean
name|multi
decl_stmt|;
DECL|field|trie
specifier|static
name|Trie
name|trie
decl_stmt|;
comment|/** no instantiation */
DECL|method|Compile
specifier|private
name|Compile
parameter_list|()
block|{}
comment|/**    * Entry point to the Compile application.    *<p>    * This program takes any number of arguments: the first is the name of the    * desired stemming algorithm to use (a list is available in the package    * description) , all of the rest should be the path or paths to a file or    * files containing a stemmer table to compile.    *     * @param args the command line arguments    */
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"System.out required: command line tool"
argument_list|)
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|1
condition|)
block|{
return|return;
block|}
name|args
index|[
literal|0
index|]
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
name|backward
operator|=
name|args
index|[
literal|0
index|]
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'-'
expr_stmt|;
name|int
name|qq
init|=
operator|(
name|backward
operator|)
condition|?
literal|1
else|:
literal|0
decl_stmt|;
name|boolean
name|storeorig
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|charAt
argument_list|(
name|qq
argument_list|)
operator|==
literal|'0'
condition|)
block|{
name|storeorig
operator|=
literal|true
expr_stmt|;
name|qq
operator|++
expr_stmt|;
block|}
name|multi
operator|=
name|args
index|[
literal|0
index|]
operator|.
name|charAt
argument_list|(
name|qq
argument_list|)
operator|==
literal|'M'
expr_stmt|;
if|if
condition|(
name|multi
condition|)
block|{
name|qq
operator|++
expr_stmt|;
block|}
name|String
name|charset
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"egothor.stemmer.charset"
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|char
name|optimizer
index|[]
init|=
operator|new
name|char
index|[
name|args
index|[
literal|0
index|]
operator|.
name|length
argument_list|()
operator|-
name|qq
index|]
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
name|optimizer
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|optimizer
index|[
name|i
index|]
operator|=
name|args
index|[
literal|0
index|]
operator|.
name|charAt
argument_list|(
name|qq
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// System.out.println("[" + args[i] + "]");
name|Diff
name|diff
init|=
operator|new
name|Diff
argument_list|()
decl_stmt|;
name|allocTrie
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
try|try
init|(
name|LineNumberReader
name|in
init|=
operator|new
name|LineNumberReader
argument_list|(
name|Files
operator|.
name|newBufferedReader
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
name|charset
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
for|for
control|(
name|String
name|line
init|=
name|in
operator|.
name|readLine
argument_list|()
init|;
name|line
operator|!=
literal|null
condition|;
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
control|)
block|{
try|try
block|{
name|line
operator|=
name|line
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|line
argument_list|)
decl_stmt|;
name|String
name|stem
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|storeorig
condition|)
block|{
name|trie
operator|.
name|add
argument_list|(
name|stem
argument_list|,
literal|"-a"
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|token
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|.
name|equals
argument_list|(
name|stem
argument_list|)
operator|==
literal|false
condition|)
block|{
name|trie
operator|.
name|add
argument_list|(
name|token
argument_list|,
name|diff
operator|.
name|exec
argument_list|(
name|token
argument_list|,
name|stem
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
name|x
parameter_list|)
block|{
comment|// no base token (stem) on a line
block|}
block|}
block|}
name|Optimizer
name|o
init|=
operator|new
name|Optimizer
argument_list|()
decl_stmt|;
name|Optimizer2
name|o2
init|=
operator|new
name|Optimizer2
argument_list|()
decl_stmt|;
name|Lift
name|l
init|=
operator|new
name|Lift
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Lift
name|e
init|=
operator|new
name|Lift
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Gener
name|g
init|=
operator|new
name|Gener
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|optimizer
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|String
name|prefix
decl_stmt|;
switch|switch
condition|(
name|optimizer
index|[
name|j
index|]
condition|)
block|{
case|case
literal|'G'
case|:
name|trie
operator|=
name|trie
operator|.
name|reduce
argument_list|(
name|g
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|"G: "
expr_stmt|;
break|break;
case|case
literal|'L'
case|:
name|trie
operator|=
name|trie
operator|.
name|reduce
argument_list|(
name|l
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|"L: "
expr_stmt|;
break|break;
case|case
literal|'E'
case|:
name|trie
operator|=
name|trie
operator|.
name|reduce
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|"E: "
expr_stmt|;
break|break;
case|case
literal|'2'
case|:
name|trie
operator|=
name|trie
operator|.
name|reduce
argument_list|(
name|o2
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|"2: "
expr_stmt|;
break|break;
case|case
literal|'1'
case|:
name|trie
operator|=
name|trie
operator|.
name|reduce
argument_list|(
name|o
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|"1: "
expr_stmt|;
break|break;
default|default:
continue|continue;
block|}
name|trie
operator|.
name|printInfo
argument_list|(
name|System
operator|.
name|out
argument_list|,
name|prefix
operator|+
literal|" "
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|DataOutputStream
name|os
init|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
name|Files
operator|.
name|newOutputStream
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|args
index|[
name|i
index|]
operator|+
literal|".out"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
init|)
block|{
name|os
operator|.
name|writeUTF
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|trie
operator|.
name|store
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|allocTrie
specifier|static
name|void
name|allocTrie
parameter_list|()
block|{
if|if
condition|(
name|multi
condition|)
block|{
name|trie
operator|=
operator|new
name|MultiTrie2
argument_list|(
operator|!
name|backward
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|trie
operator|=
operator|new
name|Trie
argument_list|(
operator|!
name|backward
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

