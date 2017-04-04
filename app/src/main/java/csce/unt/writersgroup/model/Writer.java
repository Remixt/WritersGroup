package csce.unt.writersgroup.model;

/**
 * Created by GW on 3/27/2017.
 */

public class Writer
{
    private String name;
    private int pages;

    @Override
    public String toString()
    {
        return "Writer{" +
                "name='" + name + '\'' +
                ", pages=" + pages +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Writer writer = (Writer) o;

        if (pages != writer.pages) return false;
        return name != null ? name.equals(writer.name) : writer.name == null;

    }

    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + pages;
        return result;
    }

    public String getName()
    {
        return name;
    }

    public int getPages()
    {
        return pages;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPages(int pages)
    {
        this.pages = pages;
    }
}
