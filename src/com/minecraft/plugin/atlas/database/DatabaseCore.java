package com.minecraft.plugin.atlas.database;

import java.sql.Connection;

public interface DatabaseCore {

    Connection getConnection();

    void queue(BufferStatement bs);
}
