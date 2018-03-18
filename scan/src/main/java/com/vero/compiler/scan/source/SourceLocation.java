package com.vero.compiler.scan.source;


import com.google.common.base.Objects;

import lombok.Data;


/**
 * @author XiangDe Liu qq313700046@icloud.com .
 * @version 1.5 created in 14:35 2018/3/18.
 * @since vero-compiler
 */

@Data
public class SourceLocation
{
    private Integer charIndex;

    private Integer line;

    private Integer column;

    public SourceLocation() {
        this.charIndex = 0;
        this.line = 0;
        this.column = 0;
    }

    public SourceLocation(Integer charIndex, Integer line, Integer column)
    {
        this.charIndex = charIndex;
        this.line = line;
        this.column = column;
    }

    public void incrementCharIndex()
    {
        ++this.charIndex;
    }

    public void incrementColumn(Integer tableSize)
    {
        this.column += tableSize;
    }

    public void resetColumn() {
        this.column = 0;
    }

    public void resetColumn(Integer i) {
        this.column += i;
    }

    public void increamentLine()
    {
        ++this.line;
    }

    public int CompareTo(SourceLocation other)
    {
        Integer lineDiff = getLine() - other.getLine();
        if (lineDiff > 0)
        {
            return 1;
        }
        if (lineDiff < 0)
        {
            return -1;
        }

        // same line, compare column
        Integer columnDiff = getColumn() - other.getColumn();
        if (columnDiff > 0)
        {
            return 1;
        }
        if (columnDiff < 0)
        {
            return -1;

        }
        // same line, same column.
        return 0;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceLocation that = (SourceLocation)o;
        return Objects.equal(charIndex, that.charIndex) && Objects.equal(line, that.line)
               && Objects.equal(column, that.column);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(charIndex, line, column);
    }

    @Override
    public String toString()
    {
        return "{" + "charIndex=" + charIndex + ", line=" + line + ", column=" + column + '}';
    }
}
