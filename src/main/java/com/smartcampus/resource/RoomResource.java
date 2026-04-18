package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import com.smartcampus.store.DataStore;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final DataStore store = DataStore.getInstance();

    // GET /api/v1/rooms
    @GET
    public Response getAllRooms() {
        List<Room> rooms = new ArrayList<>(store.rooms.values());
        return Response.ok(rooms).build();
    }

    // POST /api/v1/rooms
    @POST
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Room ID is required");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if (store.rooms.containsKey(room.getId())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Room with ID " + room.getId() + " already exists");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        store.rooms.put(room.getId(), room);

        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    // GET /api/v1/rooms/{roomId}
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = store.rooms.get(roomId);

        if (room == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Room not found with ID: " + roomId);
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        return Response.ok(room).build();
    }

    // DELETE /api/v1/rooms/{roomId}
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.rooms.get(roomId);

        if (room == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Room not found with ID: " + roomId);
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        // Business logic - block deletion if room has sensors
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                    "Room " + roomId + " cannot be deleted. It still has " +
                            room.getSensorIds().size() + " sensor(s) assigned to it."
            );
        }

        store.rooms.remove(roomId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Room " + roomId + " deleted successfully");
        return Response.ok(response).build();
    }
}